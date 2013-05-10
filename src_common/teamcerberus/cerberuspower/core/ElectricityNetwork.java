package teamcerberus.cerberuspower.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import teamcerberus.cerberuscore.util.CerberusLogger;
import teamcerberus.cerberuspower.api.IElectricityConductor;
import teamcerberus.cerberuspower.api.IElectricityConsumer;
import teamcerberus.cerberuspower.api.IElectricityProducer;
import teamcerberus.cerberuspower.util.ElectricityDirection;

public class ElectricityNetwork {
	private HashMap<IElectricityProducer, LinkedList<EnergyPath>>	energySourceToEnergyPathMap		= new HashMap<IElectricityProducer, LinkedList<EnergyPath>>();

	public void addTileEntity(TileEntity addedTileEntity) {
		if (!(addedTileEntity instanceof IElectricityTile)
				|| ((IElectricityTile) addedTileEntity).isAddedToElectricityNetwork()) { return; }

		if (addedTileEntity instanceof IElectricityAcceptor) {
			LinkedList<EnergyPath> reverseEnergyPaths = discover(
					addedTileEntity, true, 2147483647);

			for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
				IElectricityProducer energySource = (IElectricityProducer) reverseEnergyPath.target;

				if (energySourceToEnergyPathMap.containsKey(energySource)
						&& energySource.getElectricityOutputLimit() > reverseEnergyPath.loss) {
					energySourceToEnergyPathMap.remove(energySource);
				}
			}
		}
	}

	public void removeTileEntity(TileEntity removedTileEntity) {
		if (!(removedTileEntity instanceof IElectricityTile)
				|| !((IElectricityTile) removedTileEntity).isAddedToElectricityNetwork()) {
			boolean alreadyRemoved = !((IElectricityTile) removedTileEntity)
					.isAddedToElectricityNetwork();

			CerberusLogger.logSevere("removing " + removedTileEntity
					+ " from the ElectricityNetwork failed, already removed: "
					+ alreadyRemoved);

			return;
		}

		if (removedTileEntity instanceof IElectricityAcceptor) {
			LinkedList<EnergyPath> reverseEnergyPaths = discover(
					removedTileEntity, true, 2147483647);

			for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
				IElectricityProducer energySource = (IElectricityProducer) reverseEnergyPath.target;
				if (energySourceToEnergyPathMap.containsKey(energySource)
						&& energySource.getElectricityOutputLimit() > reverseEnergyPath.loss) {
					if (removedTileEntity instanceof IElectricityConductor) {
						energySourceToEnergyPathMap.remove(energySource);
					} else {
						for (Iterator<EnergyPath> it = energySourceToEnergyPathMap
								.get(energySource).iterator(); it.hasNext();) {
							if (it.next().target == removedTileEntity) {
								it.remove();
							}
						}
					}
				}
			}
		}

		if (removedTileEntity instanceof IElectricityProducer) {
			energySourceToEnergyPathMap.remove(removedTileEntity);
		}
	}

	@SuppressWarnings("unchecked")
	public int produceElectricity(IElectricityProducer energySource, int amount) {
		if (!energySource.isAddedToElectricityNetwork()) {
			CerberusLogger.logSevere("Electricity source: "
					+ energySource + " is not added to the network");
			return amount;
		}

		if (!energySourceToEnergyPathMap.containsKey(energySource)) {
			energySourceToEnergyPathMap.put(
					energySource,
					discover((TileEntity) energySource, false,
							energySource.getElectricityOutputLimit()));
		}

		LinkedList<EnergyPath> activeEnergyPaths = new LinkedList<EnergyPath>();
		double totalInvLoss = 0.0D;

		for (EnergyPath energyPath : energySourceToEnergyPathMap
				.get(energySource)) {
			assert energyPath.target instanceof IElectricityConsumer;

			IElectricityConsumer energySink = (IElectricityConsumer) energyPath.target;

			if (energySink.electricityWanted() > 0 && energyPath.loss < amount) {
				totalInvLoss += 1.0D / energyPath.loss;
				activeEnergyPaths.add(energyPath);
			}
		}

		Collections.shuffle(activeEnergyPaths);

		for (int i = activeEnergyPaths.size() - amount; i > 0; i--) {
			EnergyPath removedEnergyPath = activeEnergyPaths
					.remove(activeEnergyPaths.size() - 1);
			totalInvLoss -= 1.0D / removedEnergyPath.loss;
		}

		HashMap<EnergyPath, Integer> suppliedEnergyPaths = new HashMap<EnergyPath, Integer>();

		while (!activeEnergyPaths.isEmpty() && amount > 0) {
			int energyConsumed = 0;
			double newTotalInvLoss = 0.0D;

			LinkedList<EnergyPath> currentActiveEnergyPaths = (LinkedList<EnergyPath>) activeEnergyPaths
					.clone();
			activeEnergyPaths.clear();

			for (EnergyPath energyPath : currentActiveEnergyPaths) {
				IElectricityConsumer energySink = (IElectricityConsumer) energyPath.target;

				int energyProvided = (int) Math
						.floor(Math.round(amount / totalInvLoss
								/ energyPath.loss * 100000.0D) / 100000.0D);
				int energyLoss = (int) Math.floor(energyPath.loss);

				if (energyProvided > energyLoss) {
					int energyReturned = energySink.giveElectricity(
							energyPath.targetDirection, energyProvided
									- energyLoss);

					if (energyReturned == 0
							&& energySink.electricityWanted() > 0) {
						activeEnergyPaths.add(energyPath);
						newTotalInvLoss += 1.0D / energyPath.loss;
					} else if (energyReturned >= energyProvided - energyLoss) {
						energyReturned = energyProvided - energyLoss;
					}

					energyConsumed += energyProvided - energyReturned;

					int energyInjected = energyProvided - energyLoss
							- energyReturned;

					if (!suppliedEnergyPaths.containsKey(energyPath)) {
						suppliedEnergyPaths.put(energyPath,
								Integer.valueOf(energyInjected));
					} else {
						suppliedEnergyPaths.put(
								energyPath,
								Integer.valueOf(energyInjected
										+ suppliedEnergyPaths.get(energyPath)
												.intValue()));
					}
				} else {
					activeEnergyPaths.add(energyPath);
					newTotalInvLoss += 1.0D / energyPath.loss;
				}
			}

			if (energyConsumed == 0 && !activeEnergyPaths.isEmpty()) {
				EnergyPath removedEnergyPath = activeEnergyPaths
						.remove(activeEnergyPaths.size() - 1);
				newTotalInvLoss -= 1.0D / removedEnergyPath.loss;
			}

			totalInvLoss = newTotalInvLoss;
			amount -= energyConsumed;
		}

		for (Map.Entry<EnergyPath, Integer> entry : suppliedEnergyPaths
				.entrySet()) {
			EnergyPath energyPath = entry.getKey();
			int energyInjected = entry.getValue().intValue();
			energyPath.totalEnergyConducted += energyInjected;
		}
		return amount;
	}

	private LinkedList<EnergyPath> discover(TileEntity emitter,
			boolean reverse, int lossLimit) {
		HashMap<TileEntity, EnergyBlockLink> reachedTileEntities = new HashMap<TileEntity, EnergyBlockLink>();
		LinkedList<TileEntity> tileEntitiesToCheck = new LinkedList<TileEntity>();

		tileEntitiesToCheck.add(emitter);
		double currentLoss;
		while (!tileEntitiesToCheck.isEmpty()) {
			TileEntity currentTileEntity = tileEntitiesToCheck.remove();
			if (!currentTileEntity.isInvalid()) {
				currentLoss = 0.0D;

				if (currentTileEntity != emitter) {
					currentLoss = reachedTileEntities.get(currentTileEntity).loss;
				}

				LinkedList<EnergyTarget> validReceivers = getValidReceivers(
						currentTileEntity, reverse);

				for (EnergyTarget validReceiver : validReceivers) {
					if (validReceiver.tileEntity != emitter) {
						double additionalLoss = 0.0D;

						if (validReceiver.tileEntity instanceof IElectricityConductor) {
							additionalLoss = ((IElectricityConductor) validReceiver.tileEntity)
									.getConductionLoss();

							if (additionalLoss < 0.0001D) {
								additionalLoss = 0.0001D;
							}

							if (currentLoss + additionalLoss >= lossLimit) {
								;
							}
						} else if (!reachedTileEntities
								.containsKey(validReceiver.tileEntity)
								|| reachedTileEntities
										.get(validReceiver.tileEntity).loss > currentLoss
										+ additionalLoss) {
							reachedTileEntities.put(validReceiver.tileEntity,
									new EnergyBlockLink(
											validReceiver.direction,
											currentLoss + additionalLoss));

							if (validReceiver.tileEntity instanceof IElectricityConductor) {
								tileEntitiesToCheck
										.remove(validReceiver.tileEntity);
								tileEntitiesToCheck
										.add(validReceiver.tileEntity);
							}
						}
					}
				}
			}
		}
		LinkedList<EnergyPath> energyPaths = new LinkedList<EnergyPath>();
		boolean fullBreak = false;

		for (Entry<TileEntity, EnergyBlockLink> entry : reachedTileEntities
				.entrySet()) {
			if (fullBreak) {
				break;
			}
			TileEntity tileEntity = entry.getKey();

			if (!reverse && tileEntity instanceof IElectricityConsumer
					|| reverse && tileEntity instanceof IElectricityProducer) {
				EnergyBlockLink energyBlockLink = entry.getValue();

				EnergyPath energyPath = new EnergyPath();

				energyPath.loss = energyBlockLink.loss;
				energyPath.target = tileEntity;
				energyPath.targetDirection = energyBlockLink.direction;

				if (!reverse && emitter instanceof IElectricityProducer) {
					while (true) {
						tileEntity = energyBlockLink.direction
								.applyToTileEntity(tileEntity);

						if (tileEntity == emitter) {
							fullBreak = true;
							break;
						}
						if (!(tileEntity instanceof IElectricityConductor)) {
							break;
						}
						IElectricityConductor energyConductor = (IElectricityConductor) tileEntity;

						if (tileEntity.xCoord < energyPath.minX) {
							energyPath.minX = tileEntity.xCoord;
						}
						if (tileEntity.yCoord < energyPath.minY) {
							energyPath.minY = tileEntity.yCoord;
						}
						if (tileEntity.zCoord < energyPath.minZ) {
							energyPath.minZ = tileEntity.zCoord;
						}
						if (tileEntity.xCoord > energyPath.maxX) {
							energyPath.maxX = tileEntity.xCoord;
						}
						if (tileEntity.yCoord > energyPath.maxY) {
							energyPath.maxY = tileEntity.yCoord;
						}
						if (tileEntity.zCoord > energyPath.maxZ) {
							energyPath.maxZ = tileEntity.zCoord;
						}

						energyPath.conductors.add(energyConductor);

						energyBlockLink = reachedTileEntities.get(tileEntity);
						if (energyBlockLink == null) {
							CerberusLogger
									.logSevere("An electricity network pathfinding entry is corrupted");
						}

					}

					if (tileEntity != null) {
						CerberusLogger
								.logSevere("An electricity network block link is corrupted");
					}

				} else {
					energyPaths.add(energyPath);
				}
			}
		}
		return energyPaths;
	}

	private LinkedList<EnergyTarget> getValidReceivers(TileEntity emitter,
			boolean reverse) {
		LinkedList<EnergyTarget> validReceivers = new LinkedList<EnergyTarget>();

		for (ElectricityDirection direction : ElectricityDirection.directions) {
			TileEntity target = direction.applyToTileEntity(emitter);
			if (target instanceof IElectricityTile
					&& ((IElectricityTile) target).isAddedToElectricityNetwork()) {
				ElectricityDirection inverseDirection = direction.getInverse();

				if (!reverse
						&& emitter instanceof IElectricityEmitter
						&& ((IElectricityEmitter) emitter).emitsEnergyTo(
								target, direction)
						|| reverse
						&& emitter instanceof IElectricityAcceptor
						&& ((IElectricityAcceptor) emitter).acceptsEnergyFrom(
								target, direction)) {
					if (!reverse
							&& target instanceof IElectricityAcceptor
							&& ((IElectricityAcceptor) target)
									.acceptsEnergyFrom(emitter,
											inverseDirection)
							|| reverse
							&& target instanceof IElectricityEmitter
							&& ((IElectricityEmitter) target).emitsEnergyTo(
									emitter, inverseDirection)) {
						validReceivers.add(new EnergyTarget(target,
								inverseDirection));
					}
				}
			}
		}
		return validReceivers;
	}
}
