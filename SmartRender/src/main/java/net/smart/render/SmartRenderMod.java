// ==================================================================
// This file is part of Smart Render.
//
// Smart Render is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Render is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Render. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.render;

import java.lang.reflect.Method;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.smart.render.SmartRenderContext;
import net.smart.render.statistics.SmartStatisticsContext;
import net.smart.render.statistics.playerapi.SmartStatistics;
import net.smart.render.statistics.playerapi.SmartStatisticsFactory;
import net.smart.utilities.Name;
import net.smart.utilities.Reflect;

@Mod(modid = SmartRenderMod.ID, name = SmartRenderMod.NAME, version = SmartRenderMod.VERSION, useMetadata = true, clientSideOnly = true)
public class SmartRenderMod {
	static final String ID = "smartrender";
	static final String NAME = "Smart Render";
	static final String VERSION = "@VERSION@";
	
	private static boolean addRenderer = true;

	private boolean hasRenderer = false;

	public static void doNotAddRenderer()
	{
		addRenderer = false;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		hasRenderer = Loader.isModLoaded("RenderPlayerAPI");

		if(hasRenderer)
		{
			Class<?> type = Reflect.LoadClass(SmartRenderMod.class, new Name("net.smart.render.playerapi.SmartRender"), true);
			Method method = Reflect.GetMethod(type, new Name("register"));
			Reflect.Invoke(method, null);
		}

		if(!hasRenderer && addRenderer)
			SmartRenderContext.registerRenderers(RenderPlayer.class);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		SmartStatistics.register();
		SmartStatisticsFactory.initialize();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tickStart(ClientTickEvent event) {
		SmartStatisticsContext.onTickInGame();
	}
}