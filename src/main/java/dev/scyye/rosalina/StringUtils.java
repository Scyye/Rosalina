package dev.scyye.rosalina;

public class StringUtils {
	// Checks if the contents of one string, are in that order in another string and return a bool.
	public static boolean containsInOrder(String string, String contains) {
		int index = 0;
		for (int i = 0; i < contains.length(); i++) {
			char c = contains.charAt(i);
			index = string.indexOf(c, index);
			if (index == -1) {
				return false;
			}
		}
		return true;
	}

	public static boolean contains(String string, String... contains) {
		for (var str : contains) {
			if (string.contains(str)) {
				return true;
			}
		}
		return false;
	}








}
