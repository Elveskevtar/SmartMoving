package net.smart.render.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;
import net.smart.render.SRContext;
import net.smart.render.SRUtilities;
import net.smart.render.model.SRModel;

public class SRLayerElytra extends LayerElytra {
    private IRenderPlayer irp;

	public SRLayerElytra(RenderLivingBase<?> p_i47185_1_, IRenderPlayer irp) {
		super(p_i47185_1_);
		this.irp = irp;
	}

	@Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        GlStateManager.pushMatrix();
        SRModel currentModel = SRContext.getPlayerBase(irp.getModelBipedMain()).getRenderModel();
        GlStateManager.rotate(currentModel.bipedOuter.rotateAngleY * SRUtilities.RadiantToAngle, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(currentModel.bipedOuter.rotateAngleX * SRUtilities.RadiantToAngle, 1.0F, 0.0F, 0.0F);
        super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }
}
