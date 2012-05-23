package pt.fe.up.diogo.costa.runnable;

public abstract class RunnableFactory {
	public static enum Type {
		NONE ("None"),
		SIMPLE_RUNNABLE ("SimpleRunnable"),
		SCRIPTING_RUNNABLE ("ScriptingRunnable");
		
		private String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public static Type fromName(String name) {
			for(Type t : Type.values()) {
				if(t.getName().equals(name))
					return t;
			}
			
			return NONE;
		}
	}
	
	public static RunnableForInputId<?> buildRunnable(Type type) {
		RunnableForInputId<?> runnable = null;
		
		switch(type) {
		case SIMPLE_RUNNABLE:
			runnable = new SimpleRunnable();
			break;
		case SCRIPTING_RUNNABLE:
			runnable = new ScriptingRunnable();
		default:
			runnable = null;
		}
		
		return runnable;
	}
}
