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
import java.util.*;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.smart.utilities.*;

public class SmartRenderModel extends SmartRenderContext
{
	public IModelPlayer imp;
	public net.minecraft.client.model.ModelBiped mp;
	public boolean isModelPlayer;
	public boolean smallArms;

	public SmartRenderModel(boolean b, net.minecraft.client.model.ModelBiped mb, IModelPlayer imp, ModelRenderer originalBipedBody, ModelRenderer originalBipedBodywear, ModelRenderer originalBipedHead, ModelRenderer originalBipedHeadwear, ModelRenderer originalBipedRightArm, ModelRenderer originalBipedRightArmwear, ModelRenderer originalBipedLeftArm, ModelRenderer originalBipedLeftArmwear, ModelRenderer originalBipedRightLeg, ModelRenderer originalBipedRightLegwear, ModelRenderer originalBipedLeftLeg, ModelRenderer originalBipedLeftLegwear, ModelRenderer originalBipedCape, ModelRenderer originalBipedDeadmau5Head)
	{
		this.imp = imp;
		this.mp = mb;

		isModelPlayer = mp instanceof net.minecraft.client.model.ModelPlayer;
		smallArms = b;

		mb.boxList.clear();

		bipedOuter = create(null);
		bipedOuter.fadeEnabled = true;

		bipedTorso = create(bipedOuter);
		bipedBody = create(bipedTorso, originalBipedBody);
		bipedBreast = create(bipedTorso);
		bipedNeck = create(bipedBreast);
		bipedHead = create(bipedNeck, originalBipedHead);
		bipedRightShoulder = create(bipedBreast);
		bipedRightArm = create(bipedRightShoulder, originalBipedRightArm);
		bipedLeftShoulder = create(bipedBreast);
		bipedLeftShoulder.mirror = true;
		bipedLeftArm = create(bipedLeftShoulder, originalBipedLeftArm);
		bipedPelvic = create(bipedTorso);
		bipedRightLeg = create(bipedPelvic, originalBipedRightLeg);
		bipedLeftLeg = create(bipedPelvic, originalBipedLeftLeg);

		bipedBodywear = create(bipedBody, originalBipedBodywear);
		bipedHeadwear = create(bipedHead, originalBipedHeadwear);
		bipedRightArmwear = create(bipedRightArm, originalBipedRightArmwear);
		bipedLeftArmwear = create(bipedLeftArm, originalBipedLeftArmwear);
		bipedRightLegwear = create(bipedRightLeg, originalBipedRightLegwear);
		bipedLeftLegwear = create(bipedLeftLeg, originalBipedLeftLegwear);

		if(originalBipedCape != null)
		{
			bipedCloak = new ModelCapeRenderer(mb, 0, 0, bipedBreast, bipedOuter);
			copy(bipedCloak, originalBipedCape);
		}

		if(originalBipedDeadmau5Head != null)
		{
			bipedEars = new ModelEarsRenderer(mb, 24, 0, bipedHead);
			copy(bipedEars, originalBipedDeadmau5Head);
		}

		reset(); // set default rotation points

		imp.initialize(bipedBody, bipedBodywear, bipedHead, bipedHeadwear, bipedRightArm, bipedRightArmwear, bipedLeftArm, bipedLeftArmwear, bipedRightLeg, bipedRightLegwear, bipedLeftLeg, bipedLeftLegwear, bipedCloak, bipedEars);

		if(SmartRenderRender.CurrentMainModel != null)
		{
			isInventory = SmartRenderRender.CurrentMainModel.isInventory;

			totalVerticalDistance = SmartRenderRender.CurrentMainModel.totalVerticalDistance;
			currentVerticalSpeed = SmartRenderRender.CurrentMainModel.currentVerticalSpeed;
			totalDistance = SmartRenderRender.CurrentMainModel.totalDistance;
			currentSpeed = SmartRenderRender.CurrentMainModel.currentSpeed;

			distance = SmartRenderRender.CurrentMainModel.distance;
			verticalDistance = SmartRenderRender.CurrentMainModel.verticalDistance;
			horizontalDistance = SmartRenderRender.CurrentMainModel.horizontalDistance;
			currentCameraAngle = SmartRenderRender.CurrentMainModel.currentCameraAngle;
			currentVerticalAngle = SmartRenderRender.CurrentMainModel.currentVerticalAngle;
			currentHorizontalAngle = SmartRenderRender.CurrentMainModel.currentHorizontalAngle;
			prevOuterRenderData = SmartRenderRender.CurrentMainModel.prevOuterRenderData;
			isSleeping = SmartRenderRender.CurrentMainModel.isSleeping;

			actualRotation = SmartRenderRender.CurrentMainModel.actualRotation;
			forwardRotation = SmartRenderRender.CurrentMainModel.forwardRotation;
			workingAngle = SmartRenderRender.CurrentMainModel.workingAngle;
		}
	}

	private ModelRotationRenderer create(ModelRotationRenderer base)
	{
		return new ModelRotationRenderer(mp, -1, -1, base);
	}

	private ModelRotationRenderer create(ModelRotationRenderer base, ModelRenderer original)
	{
		if(original == null)
			return null;

		int textureOffsetX = (Integer)Reflect.GetField(_textureOffsetX, original);
		int textureOffsetY = (Integer)Reflect.GetField(_textureOffsetY, original);
		ModelRotationRenderer local = new ModelRotationRenderer(mp, textureOffsetX, textureOffsetY, base);
		copy(local, original);
		return local;
	}

	private static void copy(ModelRotationRenderer local, ModelRenderer original)
	{
		if(original.childModels != null)
			for(Object childModel : original.childModels)
				local.addChild((ModelRenderer)childModel);
		if(original.cubeList != null)
			for(Object cube : original.cubeList)
				local.cubeList.add((ModelBox)cube);
		local.mirror = original.mirror;
		local.isHidden = original.isHidden;
		local.showModel = original.showModel;
	}

	public void render(Entity entity, float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
	{
		GL11.glPushMatrix();
		if (entity.isSneaking())
			GL11.glTranslatef(0.0F, 0.2F, 0.0F);

		bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = true;
		if (isModelPlayer)
			bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = true;
		imp.superRender(entity, totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
		if (isModelPlayer)
			bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = false;
		bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = false;

		bipedOuter.render(factor);

		bipedOuter.renderIgnoreBase(factor);
		bipedTorso.renderIgnoreBase(factor);
		bipedBody.renderIgnoreBase(factor);
		bipedBreast.renderIgnoreBase(factor);
		bipedNeck.renderIgnoreBase(factor);
		bipedHead.renderIgnoreBase(factor);
		bipedRightShoulder.renderIgnoreBase(factor);
		bipedRightArm.renderIgnoreBase(factor);
		bipedLeftShoulder.renderIgnoreBase(factor);
		bipedLeftArm.renderIgnoreBase(factor);
		bipedPelvic.renderIgnoreBase(factor);
		bipedRightLeg.renderIgnoreBase(factor);
		bipedLeftLeg.renderIgnoreBase(factor);

		if (isModelPlayer)
		{
			bipedBodywear.renderIgnoreBase(factor);
			bipedHeadwear.renderIgnoreBase(factor);
			bipedRightArmwear.renderIgnoreBase(factor);
			bipedLeftArmwear.renderIgnoreBase(factor);
			bipedRightLegwear.renderIgnoreBase(factor);
			bipedLeftLegwear.renderIgnoreBase(factor);
		}

		GL11.glPopMatrix();
	}

	public void setRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor, Entity entity)
	{
		reset();

		if(firstPerson || isInventory)
		{
			bipedBody.ignoreBase = true;
			bipedHead.ignoreBase = true;
			bipedRightArm.ignoreBase = true;
			bipedLeftArm.ignoreBase = true;
			bipedRightLeg.ignoreBase = true;
			bipedLeftLeg.ignoreBase = true;

			if (isModelPlayer)
			{
				bipedBodywear.ignoreBase = true;
				bipedHeadwear.ignoreBase = true;
				bipedRightArmwear.ignoreBase = true;
				bipedLeftArmwear.ignoreBase = true;
				bipedRightLegwear.ignoreBase = true;
				bipedLeftLegwear.ignoreBase = true;

				bipedEars.ignoreBase = true;
				bipedCloak.ignoreBase = true;
			}

			bipedBody.forceRender = firstPerson;
			bipedHead.forceRender = firstPerson;
			bipedRightArm.forceRender = firstPerson;
			bipedLeftArm.forceRender = firstPerson;
			bipedRightLeg.forceRender = firstPerson;
			bipedLeftLeg.forceRender = firstPerson;

			if (isModelPlayer)
			{
				bipedBodywear.forceRender = firstPerson;
				bipedHeadwear.forceRender = firstPerson;
				bipedRightArmwear.forceRender = firstPerson;
				bipedLeftArmwear.forceRender = firstPerson;
				bipedRightLegwear.forceRender = firstPerson;
				bipedLeftLegwear.forceRender = firstPerson;

				bipedEars.forceRender = firstPerson;
				bipedCloak.forceRender = firstPerson;
			}

			bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
			bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
			bipedRightLeg.setRotationPoint(-2F, 12F, 0.0F);
			bipedLeftLeg.setRotationPoint(2.0F, 12F, 0.0F);

			imp.superSetRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor, entity);
			return;
		}

		if(isSleeping)
		{
			prevOuterRenderData.rotateAngleX = 0;
			prevOuterRenderData.rotateAngleY = 0;
			prevOuterRenderData.rotateAngleZ = 0;
		}

		bipedOuter.previous = prevOuterRenderData;

		bipedOuter.rotateAngleY = actualRotation / RadiantToAngle;
		bipedOuter.fadeRotateAngleY = !entity.isRiding();

		imp.animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if(isSleeping)
			imp.animateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		imp.animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if(mp.isRiding)
			imp.animateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		// TODO FIXME restore these
//		if(mp.heldItemLeft != 0)
//			imp.animateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
//
//		if(mp.heldItemRight != 0)
//			imp.animateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if(mp.swingProgress > -9990F)
		{
			imp.animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
			imp.animateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
		}

		if(mp.isSneak)
			imp.animateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		imp.animateArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		// TODO FIXME restore this
//		if(mp.aimedBow)
//			imp.animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if(bipedOuter.previous != null && !bipedOuter.fadeRotateAngleX)
			bipedOuter.previous.rotateAngleX = bipedOuter.rotateAngleX;

		if(bipedOuter.previous != null && !bipedOuter.fadeRotateAngleY)
			bipedOuter.previous.rotateAngleY = bipedOuter.rotateAngleY;

		bipedOuter.fadeIntermediate(totalTime);
		bipedOuter.fadeStore(totalTime);

		if (isModelPlayer)
		{
			bipedCloak.ignoreBase = false;
			bipedCloak.rotateAngleX = Sixtyfourth;
		}
	}

	public void animateHeadRotation(float viewHorizontalAngelOffset, float viewVerticalAngelOffset)
	{
		bipedNeck.ignoreBase = true;
		bipedHead.rotateAngleY = (actualRotation + viewHorizontalAngelOffset) / RadiantToAngle;
		bipedHead.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;
	}

	public void animateSleeping()
	{
		bipedNeck.ignoreBase = false;
		bipedHead.rotateAngleY = 0F;
		bipedHead.rotateAngleX = Eighth;
		bipedTorso.rotationPointZ = -17F;
	}

	public void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed)
	{
		bipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 2.0F * currentHorizontalSpeed * 0.5F;
		bipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed * 0.5F;

		bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed;
		bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 1.4F * currentHorizontalSpeed;
	}

	public void animateRiding()
	{
		bipedRightArm.rotateAngleX += -0.6283185F;
		bipedLeftArm.rotateAngleX += -0.6283185F;
		bipedRightLeg.rotateAngleX = -1.256637F;
		bipedLeftLeg.rotateAngleX = -1.256637F;
		bipedRightLeg.rotateAngleY = 0.3141593F;
		bipedLeftLeg.rotateAngleY = -0.3141593F;
	}

	public void animateLeftArmItemHolding()
	{
		bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemLeft;
	}

	public void animateRightArmItemHolding()
	{
		bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemRight;
//		if(mp.heldItemRight == 3)
//			bipedRightArm.rotateAngleY = -0.5235988F;
	}

	public void animateWorkingBody()
	{
		float angle = MathHelper.sin(MathHelper.sqrt(mp.swingProgress) * Whole) * 0.2F;
		bipedBreast.rotateAngleY = bipedBody.rotateAngleY += angle;
		bipedBreast.rotationOrder = bipedBody.rotationOrder = ModelRotationRenderer.YXZ;
		bipedLeftArm.rotateAngleX += angle;
	}

	public void animateWorkingArms()
	{
		float f6 = 1.0F - mp.swingProgress;
		f6 = 1.0F - f6 * f6 * f6;
		float f7 = MathHelper.sin(f6 * Half);
		float f8 = MathHelper.sin(mp.swingProgress * Half) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
		bipedRightArm.rotateAngleX -= f7 * 1.2D + f8;
		bipedRightArm.rotateAngleY += MathHelper.sin(MathHelper.sqrt(mp.swingProgress) * Whole) * 0.4F;
		bipedRightArm.rotateAngleZ -= MathHelper.sin(mp.swingProgress * Half) * 0.4F;
	}

	public void animateSneaking()
	{
		bipedTorso.rotateAngleX += 0.5F;
		bipedRightLeg.rotateAngleX += -0.5F;
		bipedLeftLeg.rotateAngleX += -0.5F;
		bipedRightArm.rotateAngleX += -0.1F;
		bipedLeftArm.rotateAngleX += -0.1F;

		bipedPelvic.offsetY = -0.13652F;
		bipedPelvic.offsetZ = -0.05652F;

		bipedBreast.offsetY = -0.01872F;
		bipedBreast.offsetZ = -0.07502F;

		bipedNeck.offsetY = 0.0621F;
	}

	public void animateArms(float totalTime)
	{
		bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
	}

	public void animateBowAiming(float totalTime)
	{
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY;
		bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F - bipedOuter.rotateAngleY;
		bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
		bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
		bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
	}

	public void reset()
	{
		bipedOuter.reset();
		bipedTorso.reset();
		bipedBody.reset();
		bipedBreast.reset();
		bipedNeck.reset();
		bipedHead.reset();
		bipedRightShoulder.reset();
		bipedRightArm.reset();
		bipedLeftShoulder.reset();
		bipedLeftArm.reset();
		bipedPelvic.reset();
		bipedRightLeg.reset();
		bipedLeftLeg.reset();

		if (isModelPlayer)
		{
			bipedBodywear.reset();
			bipedHeadwear.reset();
			bipedRightArmwear.reset();
			bipedLeftArmwear.reset();
			bipedRightLegwear.reset();
			bipedLeftLegwear.reset();

			bipedEars.reset();
			bipedCloak.reset();
		}

		bipedRightShoulder.setRotationPoint(-5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
		bipedLeftShoulder.setRotationPoint(5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
		bipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
		bipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
		bipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);

		if (isModelPlayer)
			bipedCloak.setRotationPoint(0.0F, 0.0F, 2.0F);
	}

	public void renderCloak(float f)
	{
		attemptToCallRenderCape = true;
		if(!disabled)
			imp.superRenderCloak(f);
	}

	public ModelRenderer getRandomBox(Random par1Random)
	{
		List<?> boxList = mp.boxList;
		int size = boxList.size();
		int renderersWithBoxes = 0;

		for(int i=0; i<size; i++)
		{
			ModelRenderer renderer = (ModelRenderer)boxList.get(i);
			if(canBeRandomBoxSource(renderer))
				renderersWithBoxes++;
		}

		if(renderersWithBoxes != 0)
		{
			int random = par1Random.nextInt(renderersWithBoxes);
			renderersWithBoxes = -1;

			for(int i=0; i<size; i++)
			{
				ModelRenderer renderer = (ModelRenderer)boxList.get(i);
				if(canBeRandomBoxSource(renderer))
					renderersWithBoxes++;
				if(renderersWithBoxes == random)
					return renderer;
			}
		}

		return null;
	}

	private static boolean canBeRandomBoxSource(ModelRenderer renderer)
	{
		return renderer.cubeList != null && renderer.cubeList.size() > 0 && (!(renderer instanceof ModelRotationRenderer) || ((ModelRotationRenderer)renderer).canBeRandomBoxSource());
	}

	public boolean isInventory;

	public int scaleArmType;
	public int scaleLegType;

	public float totalVerticalDistance;
	public float currentVerticalSpeed;
	public float totalDistance;
	public float currentSpeed;

	public double distance;
	public double verticalDistance;
	public double horizontalDistance;
	public float currentCameraAngle;
	public float currentVerticalAngle;
	public float currentHorizontalAngle;

	public float actualRotation;
	public float forwardRotation;
	public float workingAngle;

	public ModelRotationRenderer bipedOuter;
	public ModelRotationRenderer bipedTorso;
	public ModelRotationRenderer bipedBody;
	public ModelRotationRenderer bipedBreast;
	public ModelRotationRenderer bipedNeck;
	public ModelRotationRenderer bipedHead;
	public ModelRotationRenderer bipedRightShoulder;
	public ModelRotationRenderer bipedRightArm;
	public ModelRotationRenderer bipedLeftShoulder;
	public ModelRotationRenderer bipedLeftArm;
	public ModelRotationRenderer bipedPelvic;
	public ModelRotationRenderer bipedRightLeg;
	public ModelRotationRenderer bipedLeftLeg;

	public ModelRotationRenderer bipedBodywear;
	public ModelRotationRenderer bipedHeadwear;
	public ModelRotationRenderer bipedRightArmwear;
	public ModelRotationRenderer bipedLeftArmwear;
	public ModelRotationRenderer bipedRightLegwear;
	public ModelRotationRenderer bipedLeftLegwear;

	public ModelEarsRenderer bipedEars;
	public ModelCapeRenderer bipedCloak;


	public boolean disabled;
	public boolean attemptToCallRenderCape;
	public RendererData prevOuterRenderData;
	public boolean isSleeping;
	public boolean firstPerson;

	private static final Field _textureOffsetX = Reflect.GetField(ModelRenderer.class, SmartRenderInstall.ModelRenderer_textureOffsetX);
	private static final Field _textureOffsetY = Reflect.GetField(ModelRenderer.class, SmartRenderInstall.ModelRenderer_textureOffsetY);
}