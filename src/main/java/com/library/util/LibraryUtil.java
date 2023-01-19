package com.library.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LibraryUtil {
	private final static String file = "library.png";

	public static void setIcon(Stage stage) {
		stage.getIcons().add(new Image(String.valueOf(LibraryUtil.class.getResource(file))));		
	}
}
