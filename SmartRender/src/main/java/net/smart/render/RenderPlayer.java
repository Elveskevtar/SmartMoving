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

import java.lang.reflect.*;

import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.smart.utilities.*;

public class RenderPlayer extends net.minecraft.client.renderer.entity.RenderPlayer implements IRenderPlayer
{
	public RenderPlayer(RenderManager renderManager)
	{
		this(renderManager, false);
	}

	public RenderPlayer(RenderManager renderManager, boolean b)
	{
		super(renderManager, b);

		render = new SmartRenderRender(this);
	}

	@Override
	public IModelPlayer createModel(net.minecraft.client.model.ModelBiped existing, float f, boolean b)
	{
		if(existing instanceof net.minecraft.client.model.ModelPlayer)
			return new net.smart.render.ModelPlayer(existing, f, b);
		return new net.smart.render.ModelBiped(existing, f);
	}

	@Override
	public boolean getSmallArms()
	{
		return (Boolean)Reflect.GetField(net.minecraft.client.renderer.entity.RenderPlayer.class, this, SmartRenderInstall.RenderPlayer_smallArms);
	}

	@Override
	public void initialize(net.minecraft.client.model.ModelPlayer modelBipedMain, net.minecraft.client.model.ModelBiped modelArmor)
	{
		mainModel = modelBipedMain;

		for (Object layer : this.layerRenderers)
		{
			if (layer instanceof LayerArmorBase)
				Reflect.SetField(_modelArmor, layer, modelArmor);
			if (layer instanceof LayerCustomHead)
				Reflect.SetField(_playerHead, layer, modelBipedMain.bipedHead);
		}
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		render.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	protected void applyRotations(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		render.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.applyRotations(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	protected void renderLayers(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5, float f6, float f7)
	{
		render.renderSpecials(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void superRenderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5, float f6, float f7)
	{
		super.renderLayers(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	protected float handleRotationFloat(AbstractClientPlayer entityPlayer, float f)
	{
		render.beforeHandleRotationFloat(entityPlayer, f);
		float result = super.handleRotationFloat(entityPlayer, f);
		render.afterHandleRotationFloat(entityPlayer, f);
		return result;
	}

	@Override
	public RenderManager getRenderRenderManager()
	{
		return renderManager;
	}

	@Override
	public net.minecraft.client.model.ModelPlayer getModelBipedMain()
	{
		return this.getMainModel();
	}

	@Override
	public net.minecraft.client.model.ModelBiped getModelArmor()
	{
		for (Object layer : this.layerRenderers)
			if (layer instanceof LayerArmorBase)
				return (net.minecraft.client.model.ModelBiped)Reflect.GetField(_modelArmor, layer);
		return null;
	}

	public IModelPlayer getRenderModelBipedMain()
	{
		return (IModelPlayer)getModelBipedMain();
	}

	public IModelPlayer getRenderModelArmor()
	{
		return (IModelPlayer)getModelArmor();
	}

	@Override
	public IModelPlayer[] getRenderModels()
	{
		if(allIModelPlayers == null)
			allIModelPlayers = new IModelPlayer[] { getRenderModelBipedMain(), getRenderModelArmor() };
		return allIModelPlayers;
	}

	private IModelPlayer[] allIModelPlayers;

	private final SmartRenderRender render;

	private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmor);
	private final static Field _playerHead = Reflect.GetField(LayerCustomHead.class, SmartRenderInstall.LayerCustomHead_playerHead);
}