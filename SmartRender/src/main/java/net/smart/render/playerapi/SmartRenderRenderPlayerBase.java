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

package net.smart.render.playerapi;

import java.lang.reflect.*;

import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import api.player.model.IModelPlayerAPI;
import api.player.render.*;
import net.smart.render.*;
import net.smart.utilities.*;

public class SmartRenderRenderPlayerBase extends RenderPlayerBase implements net.smart.render.IRenderPlayer
{
	public SmartRenderRenderPlayerBase(RenderPlayerAPI renderPlayerAPI)
	{
		super(renderPlayerAPI);
	}

	public SmartRenderRender getRenderRender()
	{
		if (render == null)
			render = new SmartRenderRender(this);
		return render;
	}
	
	public static SmartRenderModelPlayerBase getPlayerBase(
			net.minecraft.client.model.ModelBiped modelPlayer) {
		return (SmartRenderModelPlayerBase) ((IModelPlayerAPI) modelPlayer)
				.getModelPlayerBase(SmartRenderInfo.ModName);
	}

	@Override
	public IModelPlayer createModel(net.minecraft.client.model.ModelBiped existing, float f, boolean b)
	{
		return getPlayerBase(existing);
	}

	@Override
	public boolean getSmallArms()
	{
		return renderPlayerAPI.getSmallArmsField();
	}

	@Override
	public void initialize(net.minecraft.client.model.ModelPlayer modelBipedMain, net.minecraft.client.model.ModelBiped modelArmor)
	{
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		getRenderRender().doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		getRenderRender().rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void renderLayers(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5, float f6, float f7)
	{
		getRenderRender().renderSpecials(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void superRenderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5, float f6, float f7)
	{
		super.renderLayers(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void beforeHandleRotationFloat(AbstractClientPlayer entityliving, float f)
	{
		getRenderRender().beforeHandleRotationFloat(entityliving, f);
	}

	@Override
	public void afterHandleRotationFloat(AbstractClientPlayer entityliving, float f)
	{
		getRenderRender().afterHandleRotationFloat(entityliving, f);
	}

	@Override
	public RenderManager getRenderRenderManager()
	{
		return renderPlayerAPI.getRenderManagerField();
	}

	@Override
	public net.minecraft.client.model.ModelPlayer getModelBipedMain()
	{
		return renderPlayer.getMainModel();
	}

	@Override
	public net.minecraft.client.model.ModelBiped getModelArmor()
	{
		for (Object layer : renderPlayerAPI.getLayerRenderersField())
			if (layer instanceof LayerArmorBase)
				return (net.minecraft.client.model.ModelBiped)Reflect.GetField(_modelArmor, layer);
		return null;
	}

	@Override
	public IModelPlayer[] getRenderModels()
	{
		net.minecraft.client.model.ModelBiped[] modelPlayers = api.player.model.ModelPlayerAPI.getAllInstances();
		if(allModelPlayers != null && (allModelPlayers == modelPlayers || modelPlayers.length == 0 && allModelPlayers.length == 0))
			return allIModelPlayers;

		allModelPlayers = modelPlayers;
		allIModelPlayers = new IModelPlayer[modelPlayers.length];
		for(int i=0; i<allIModelPlayers.length; i++)
			allIModelPlayers[i] = getPlayerBase(allModelPlayers[i]);
		return allIModelPlayers;
	}

	private net.minecraft.client.model.ModelBiped[] allModelPlayers;
	private IModelPlayer[] allIModelPlayers;

	private SmartRenderRender render;

	private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmor);
}