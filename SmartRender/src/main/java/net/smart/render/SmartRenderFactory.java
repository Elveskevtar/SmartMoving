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

import java.util.*;

import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.fml.client.registry.*;
import net.smart.utilities.*;

public class SmartRenderFactory<T extends Render<AbstractClientPlayer>> implements IRenderFactory<AbstractClientPlayer>
{
	private final Class<T> type;

	public SmartRenderFactory(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public Render<AbstractClientPlayer> createRenderFor(RenderManager manager)
	{
		Render<AbstractClientPlayer> defaultRender;
		try
		{
			defaultRender = type.getConstructor(RenderManager.class).newInstance(manager);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create player renderer", e);
		}

		Render<AbstractClientPlayer> slimRender;
		try
		{
			slimRender = type.getConstructor(RenderManager.class, boolean.class).newInstance(manager, true);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create player slim renderer", e);
		}

		Reflect.SetField(RenderManager.class, manager, SmartRenderInstall.RenderManager_renderPlayer, defaultRender);

		Map skinMap = (Map)Reflect.GetField(RenderManager.class, manager, SmartRenderInstall.RenderManager_skinMap);
		skinMap.put("default", defaultRender);
		skinMap.put("slim", slimRender);

		return defaultRender;
	}

	public static <T extends Render<AbstractClientPlayer>> void registerRenderers(final Class<T> type)
	{
		RenderingRegistry.registerEntityRenderingHandler(AbstractClientPlayer.class, new SmartRenderFactory<T>(type));
	}
}