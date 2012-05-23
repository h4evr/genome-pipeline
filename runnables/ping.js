/**
 * Ping runnable
 */
function run() {
	var cmd = "ping -n 1 " + self.getInputId();	
	return cmd;
}

run();