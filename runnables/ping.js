/**
 * Ping runnable
 */
function run() {
	var utils = ScriptingUtils.newInstance();
	var job = jobDao.getJobById(self.getJobId());
	
	if(!job) {
		return "Couldn't fetch job!";
	}
	
	var cmd = utils.executeProgram("ping", ["-c", "1", "127.0.0." + job.getInputId().toString()]);
	
	if(cmd.hasError()) {
		cmd.getException().printStackTrace();
		return "Error!";
	} else {
		return cmd.getValue();
	}
}

run();