package co.q64.emotion.lang;

import co.q64.emotion.lang.value.Null;
import co.q64.emotion.lang.value.Value;
import com.google.auto.factory.Provided;
import lombok.Data;

import javax.inject.Inject;
import java.util.Arrays;

@Data
public class Registers {
    private Value[] longRegister = new Value[256];
    private Value a, b, c, d, i, o;

    @Inject
    protected Registers(Null nul) {
        this.a = nul;
        this.b = nul;
        this.c = nul;
        this.d = nul;
        this.i = nul;
        this.o = nul;
        Arrays.fill(longRegister, nul);
    }
}
