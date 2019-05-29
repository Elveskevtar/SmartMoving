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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class SMPacketStream {
	private static final byte StatePacketId = 0;
	private static final byte ConfigInfoPacketId = 1;
	private static final byte ConfigContentPacketId = 2;
	private static final byte ConfigChangePacketId = 3;
	private static final byte SpeedChangePacketId = 4;
	private static final byte HungerChangePacketId = 5;
	private static final byte SoundPacketId = 6;

	public static final String Id;
	public static final Set<StackTraceElement> errors;

	static {
		String id = SMInfo.ModComId;
		if (id.length() > 15)
			id = id.substring(0, 15);
		Id = id;
		errors = new HashSet<StackTraceElement>();
	}

	public static void receivePacket(FMLProxyPacket packet, IPacketReceiver comm, IEntityPlayerMP player) {
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(packet.payload().array());
			ObjectInputStream objectInput = new ObjectInputStream(byteInput);
			byte packetId = objectInput.readByte();
			switch (packetId) {
			case StatePacketId:
				int entityId = objectInput.readInt();
				long state = objectInput.readLong();
				comm.processStatePacket(packet, player, entityId, state);
				break;
			case ConfigInfoPacketId:
				String info = (String) objectInput.readObject();
				comm.processConfigInfoPacket(packet, player, info);
				break;
			case ConfigContentPacketId:
				String[] content = (String[]) objectInput.readObject();
				String username = (String) objectInput.readObject();
				comm.processConfigContentPacket(packet, player, content, username);
				break;
			case ConfigChangePacketId:
				comm.processConfigChangePacket(packet, player);
				break;
			case SpeedChangePacketId:
				int difference = objectInput.readInt();
				username = (String) objectInput.readObject();
				comm.processSpeedChangePacket(packet, player, difference, username);
				break;
			case HungerChangePacketId:
				float hunger = objectInput.readFloat();
				comm.processHungerChangePacket(packet, player, hunger);
				break;
			case SoundPacketId:
				String soundId = (String) objectInput.readObject();
				float volume = objectInput.readFloat();
				float pitch = objectInput.readFloat();
				comm.processSoundPacket(packet, player, soundId, volume, pitch);
				break;
			default:
				throw new RuntimeException("Unknown packet id '" + packetId + "' found");
			}
		} catch (Throwable t) {
			if (errors.add(t.getStackTrace()[0]))
				t.printStackTrace();
			else
				System.err.println(t.getClass().getName() + ": " + t.getMessage());
		}
	}

	public static void sendState(IPacketSender comm, int entityId, long state) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(StatePacketId);
			objectOutput.writeInt(entityId);
			objectOutput.writeLong(state);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendConfigInfo(IPacketSender comm, String info) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(ConfigInfoPacketId);
			objectOutput.writeObject(info);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendConfigContent(IPacketSender comm, String[] content, String username) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(ConfigContentPacketId);
			objectOutput.writeObject(content);
			objectOutput.writeObject(username);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendConfigChange(IPacketSender comm) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(ConfigChangePacketId);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendSpeedChange(IPacketSender comm, int difference, String username) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(SpeedChangePacketId);
			objectOutput.writeInt(difference);
			objectOutput.writeObject(username);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendHungerChange(IPacketSender comm, float hunger) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(HungerChangePacketId);
			objectOutput.writeFloat(hunger);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}

	public static void sendSound(IPacketSender comm, String soundId, float volume, float pitch) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeByte(SoundPacketId);
			objectOutput.writeObject(soundId);
			objectOutput.writeFloat(volume);
			objectOutput.writeFloat(pitch);
			objectOutput.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		comm.sendPacket(byteOutput.toByteArray());
	}
}
