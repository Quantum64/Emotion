package co.q64.emotion;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import co.q64.emotion.DaggerEmotionMain_EmotionMainComponent;
import co.q64.emotion.EmotionEngine;
import co.q64.emotion.compiler.CompilerOutput;
import co.q64.emotion.inject.StandardModule;
import co.q64.emotion.inject.SystemModule;
import co.q64.emotion.lang.opcode.Opcodes;
import co.q64.emotion.runtime.Output;
import co.q64.emotion.DaggerBenchmarkTest_OpcodesComponent;
import dagger.Component;
import lombok.AllArgsConstructor;

public class BenchmarkTest {
	@Test
	public void benchmark() throws Exception {
		Options opt = new OptionsBuilder()
		// @formatter:off
				.include(this.getClass().getName() + ".*")
				.mode(Mode.AverageTime)
				.timeUnit(TimeUnit.MILLISECONDS)
				.warmupTime(TimeValue.milliseconds(100))
				.warmupIterations(0)
				.measurementTime(TimeValue.milliseconds(100))
				.measurementIterations(1)
				.threads(4)
				.forks(1)
				.shouldFailOnError(true)
				.shouldDoGC(true)
				.build();
		// @formatter:on
		new Runner(opt).run();
	}

	@Benchmark
	public void benchmarkOpcodeRegistration(Blackhole blackhole) {
		Opcodes opcodes = DaggerBenchmarkTest_OpcodesComponent.create().getOpcodes();
		blackhole.consume(opcodes);
	}

	@Benchmark
	public void benchmarkHelloWorld(Blackhole blackhole) {
		EmotionEngine jstx = DaggerEmotionMain_EmotionMainComponent.create().getJstx();
		CompilerOutput compiled = jstx.compileProgram(Arrays.asList("load Hello,", "load World!", "flatten soft"));
		Assert.assertEquals(true, compiled.isSuccess());
		jstx.runProgram(compiled.getProgram(), "", new BlackholeOutput(blackhole));
	}

	@AllArgsConstructor
	private static class BlackholeOutput implements Output {
		private Blackhole blackhole;

		@Override
		public void print(String message) {
			blackhole.consume(message);
		}

		@Override
		public void println(String message) {
			print(message);
		}
	}

	@Singleton
	@Component(modules = { SystemModule.class, StandardModule.class })
	protected static interface OpcodesComponent {
		public Opcodes getOpcodes();
	}
}
