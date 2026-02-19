package com.thepinkhacker.decree.gametest;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.lang.reflect.Method;

public class ToggleDownfallCommandTest implements CustomTestMethodInvoker {
    private static final int WAIT_AMOUNT = 100;

    @GameTest(environment = "decree:toggledownfall_clear", setupTicks = WAIT_AMOUNT, maxTicks = WAIT_AMOUNT)
	public void testClear(DecreeGameTestHelper context) {
        context.getContext()
                .startSequence()
                .thenExecute(context::assertWeatherClear)
                .thenWaitUntil(() -> context.executeCommand("toggledownfall"))
                .thenExecuteAfter(WAIT_AMOUNT, context::assertWeatherRain)
                .thenSucceed();
	}

    @GameTest(environment = "decree:toggledownfall_rain", setupTicks = WAIT_AMOUNT, maxTicks = WAIT_AMOUNT)
	public void testRain(DecreeGameTestHelper context) {
        context.getContext()
                .startSequence()
                .thenExecute(context::assertWeatherRain)
                .thenWaitUntil(() -> context.executeCommand("toggledownfall"))
                .thenExecuteAfter(WAIT_AMOUNT, context::assertWeatherClear)
                .thenSucceed();
	}

    @GameTest(environment = "decree:toggledownfall_thunder", setupTicks = WAIT_AMOUNT, maxTicks = WAIT_AMOUNT)
	public void testThunder(DecreeGameTestHelper context) {
        context.getContext()
                .startSequence()
                .thenExecute(context::assertWeatherThunder)
                .thenWaitUntil(() -> context.executeCommand("toggledownfall"))
                .thenExecuteAfter(WAIT_AMOUNT, context::assertWeatherClear)
                .thenSucceed();
	}

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
        method.invoke(this, DecreeGameTestHelper.of(context));
    }
}
