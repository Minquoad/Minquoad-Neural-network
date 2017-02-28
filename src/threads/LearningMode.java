package threads;

public enum LearningMode {

	SIMPLE("Basic"),
	WITH_CONTROL_SAMPLE("Learn with a control sample");

	private String name = "";

	private LearningMode(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static LearningMode getLearningModeByName(String name) {
		LearningMode valueFounded = null;
		for (LearningMode learningMode : LearningMode.values()) {
			if (learningMode.toString().equals(name)) {
				valueFounded = learningMode;
			}
		}
		return valueFounded;
	}

}
