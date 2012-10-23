package emodroid.command;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventFactory;

import javolution.io.Struct;

public class SMSCommand extends Struct {

	public final Enum32<SMSCommandType> type = new Enum32<SMSCommandType>(
			SMSCommandType.values());
	public final SMSCommandEvent cmd = inner(new SMSCommandEvent());

	private int getSize(Struct s) {
		return (this.size() - cmd.size()) + s.size();
	}

	public int getSize() {
		SMSCommandType type = (SMSCommandType) this.type.get();
		return getSize(type.getStruct());
	}

	@Override
	public String toString() {
		return "SMS Command [type=" + type.get() + "]";
	}

	public final static EventFactory<SMSCommand> FACTORY = new EventFactory<SMSCommand>() {
		@Override
		public SMSCommand newInstance() {
			SMSCommand cmd = new SMSCommand();
			cmd.setByteBuffer(ByteBuffer.allocate(cmd.size()), 0);
			return cmd;
		}
	};
}
