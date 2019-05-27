// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package api.player.render;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public final class LayerPlayerArmorClassVisitor extends ClassVisitor
{
	public static final String targetClassName = "api.player.render.LayerPlayerArmor";
	public static final String obfuscatedClassReference = "api/player/render/LayerPlayerArmor";
	public static final String obfuscatedSuperClassReference = "cbn";
	public static final String deobfuscatedClassReference = "api/player/render/LayerPlayerArmor";
	public static final String deobfuscateSuperClassReference = "net/minecraft/client/renderer/entity/layers/LayerBipedArmor";

	public static byte[] transform(byte[] bytes, boolean isObfuscated, Map<String, Stack<String>> constructorReplacements)
	{
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			LayerPlayerArmorClassVisitor p = new LayerPlayerArmorClassVisitor(cw, isObfuscated, constructorReplacements);

			cr.accept(p, 0);

			byte[] result = cw.toByteArray();
			in.close();
			return result;
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	private final boolean isObfuscated;
	private final Map<String, Stack<String>> constructorReplacements;

	public LayerPlayerArmorClassVisitor(ClassVisitor classVisitor, boolean isObfuscated, Map<String, Stack<String>> constructorReplacements)
	{
		super(262144, classVisitor);
		this.isObfuscated = isObfuscated;
		this.constructorReplacements = constructorReplacements;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		if(isObfuscated && superName.equals("net/minecraft/client/renderer/entity/layers/LayerBipedArmor"))
			superName = "ccb";
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		return new LayerPlayerArmorConstructorVisitor(super.visitMethod(access, name, desc, signature, exceptions), isObfuscated, constructorReplacements);
	}
}
