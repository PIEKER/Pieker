package pieker.common.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class HttpTest {

    @Test
    void testSend(){
        String result = Http.send("HttpTest","https://github.com/PIEKER/Pieker","GET",
                0,0,"","");
        assertNotEquals("ERROR ON REQUEST", result);
    }
}