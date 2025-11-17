package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import view.App;

public class AppTest {

    @Test
    public void testAppLaunch() {
        App app = new App();
        assertNotNull(app, "App should be initialized");
    }

    @Test
    public void testMainMethod() {
        // Ensure the main method runs without exceptions
        assertDoesNotThrow(() -> App.main(new String[] {}));
    }
}
