// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import java.io.File;
import java.util.List;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.smart.moving.config.SmartMovingConfig;
import net.smart.moving.config.SmartMovingOptions;
import net.smart.utilities.Reflect;

@Mod(modid = SmartMovingMod.ID, name = SmartMovingMod.NAME, version = SmartMovingMod.VERSION, useMetadata = true)
public class SmartMovingMod {
	final static String ID = "smartmoving";
	final static String NAME = "Smart Moving";
	final static String VERSION = "@VERSION@";

	protected static String ModComVersion = "2.4";

	private final boolean isClient;

	private boolean hasRenderer = false;

	public SmartMovingMod() {
		isClient = FMLCommonHandler.instance().getSide().isClient();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (isClient)
			net.smart.moving.render.playerapi.SmartMoving.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE
				.newEventDrivenChannel(SmartMovingPacketStream.Id)
				.register(this);

		if (isClient) {
			SmartMovingPlayerBase.registerPlayerBase();
			SmartMovingServerPlayerBase.registerPlayerBase();
			SmartMovingServerComm.localUserNameProvider = new LocalUserNameProvider();
			MinecraftForge.EVENT_BUS.register(this);
			SmartMovingFactory.initialize();
			checkForMods();
			SmartMovingContext.initialize();
		} else
			SmartMovingServer.initialize(
					new File("."), FMLCommonHandler.instance()
							.getMinecraftServerInstance().getGameType().getID(),
					new SmartMovingConfig());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (!isClient)
			net.smart.moving.SmartMovingServerPlayerBase.registerPlayerBase();
	}

	@SubscribeEvent
	public void tickStart(ClientTickEvent event) {
		SmartMovingContext.onTickInGame();
	}

	@SubscribeEvent
	public void onPacketData(ServerCustomPacketEvent event) {
		SmartMovingPacketStream.receivePacket(event.getPacket(),
				SmartMovingServerComm.instance,
				net.smart.moving.SmartMovingServerPlayerBase.getPlayerBase(
						((NetHandlerPlayServer) event.getHandler()).player));
	}

	@SubscribeEvent
	public void onPacketData(ClientCustomPacketEvent event) {
		SmartMovingPacketStream.receivePacket(event.getPacket(),
				SmartMovingComm.instance, null);
	}

	public void checkForMods() {
		List<ModContainer> modList = Loader.instance().getActiveModList();
		boolean hasRedPowerWiring = false;
		boolean hasBuildCraftTransport = false;
		boolean hasFiniteLiquid = false;
		boolean hasBetterThanWolves = false;
		boolean hasSinglePlayerCommands = false;
		boolean hasRopesPlus = false;
		boolean hasASGrapplingHook = false;
		boolean hasBetterMisc = false;

		for (int i = 0; i < modList.size(); i++) {
			ModContainer mod = modList.get(i);
			String name = mod.getName();

			if (name.contains("RedPowerWiring"))
				hasRedPowerWiring = true;
			else if (name.contains("BuildCraftTransport"))
				hasBuildCraftTransport = true;
			else if (name.contains("Liquid"))
				hasFiniteLiquid = true;
			else if (name.contains("FCBetterThanWolves"))
				hasBetterThanWolves = true;
			else if (name.contains("SinglePlayerCommands"))
				hasSinglePlayerCommands = true;
			else if (name.contains("ASGrapplingHook"))
				hasASGrapplingHook = true;
			else if (name.contains("BetterMisc"))
				hasBetterMisc = true;
		}

		hasRopesPlus = Reflect.CheckClasses(SmartMovingMod.class,
				SmartMovingInstall.RopesPlusCore);

		SmartMovingOptions.initialize(hasRedPowerWiring, hasBuildCraftTransport,
				hasFiniteLiquid, hasBetterThanWolves, hasSinglePlayerCommands,
				hasRopesPlus, hasASGrapplingHook, hasBetterMisc);
	}
}