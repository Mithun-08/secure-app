package com.stackbill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    public void testApplicationOutput() {
        App app = new App();
        // This generates the test result output file during 'mvn test'
        assertEquals("Hello World! Secure Pipeline Verified.", app.getGreeting());
    }
}
