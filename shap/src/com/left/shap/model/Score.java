package com.left.shap.model;

public class Score implements Comparable<Score> {
	private final String name;
	private final double score;

	public Score(String name, double score) {
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public double getScore() {
		return score;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Score) {
			Score s = (Score) obj;
			return this.name.equals(s.name);
		}
		return false;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object. Note: this
	 * class has a natural ordering that is inconsistent with equals The ordering is based on the score. Equality is based on the name.
	 */
	@Override
	public int compareTo(Score s) {
		return Double.compare(this.score, s.score);
	}
}
