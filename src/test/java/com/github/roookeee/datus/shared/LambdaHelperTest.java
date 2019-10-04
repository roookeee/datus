package com.github.roookeee.datus.shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

class LambdaHelperTest {

    private static boolean OPTIMIZE_ORIGINAL_VALUE;

    @BeforeEach
    public void init() {
        LambdaHelper.WHITELISTED_CALLEES.add(LambdaHelperTest.class.getName());
        OPTIMIZE_ORIGINAL_VALUE = LambdaHelper.OPTIMIZE;
    }

    @AfterEach
    public void reset() {
        LambdaHelper.OPTIMIZE = OPTIMIZE_ORIGINAL_VALUE;
    }

    @Test
    public void whiteListShouldWorkCorrectly() {
        LambdaHelper.WHITELISTED_CALLEES.remove(LambdaHelperTest.class.getName());

        assertThatThrownBy(() -> LambdaHelper.nullsafe(a -> a))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> LambdaHelper.andThen(a -> a, a -> a))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> LambdaHelper.andThen((a,b) -> a, (a,b) -> a))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> LambdaHelper.toBiFunction(a -> a, (a,b) -> {}))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void optimizedNullsafeFunctionShouldBehaveCorrectly() {
        assumeThat(LambdaHelper.OPTIMIZE)
                .withFailMessage("JDK running this test suite doesn't support optimization")
                .isTrue();

        //given
        Function<String, Integer> nullSafeFn = LambdaHelper.nullsafe(String::length);

        //when
        Integer nullResult = nullSafeFn.apply(null);
        Integer normalResult = nullSafeFn.apply("1");

        //then
        assertThat(nullSafeFn).isNotInstanceOf(LambdaHelper.NullSafeFunction.class);
        assertThat(nullResult).isNull();
        assertThat(normalResult).isEqualTo(1);
    }

    @Test
    public void normalNullsafeFunctionShouldBehaveCorrectly() {
        LambdaHelper.OPTIMIZE = false;

        //given
        Function<String, Integer> nullSafeFn = LambdaHelper.nullsafe(String::length);

        //when
        Integer nullResult = nullSafeFn.apply(null);
        Integer normalResult = nullSafeFn.apply("1");

        //then
        assertThat(nullSafeFn).isInstanceOf(LambdaHelper.NullSafeFunction.class);
        assertThat(nullResult).isNull();
        assertThat(normalResult).isEqualTo(1);
    }

    @Test
    public void optimizedAndThenShouldBehaveCorrectly() {
        assumeThat(LambdaHelper.OPTIMIZE)
                .withFailMessage("JDK running this test suite doesn't support optimization")
                .isTrue();

        //given
        Function<Object, Integer> fnChain = LambdaHelper.andThen(Object::toString, String::length);

        //when
        Integer result = fnChain.apply(777);

        //then
        assertThat(fnChain).isNotInstanceOf(LambdaHelper.FunctionCombinator.class);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void normalAndThenShouldBehaveCorrectly() {
        LambdaHelper.OPTIMIZE = false;

        //given
        Function<Object, Integer> fnChain = LambdaHelper.andThen(Object::toString, String::length);

        //when
        Integer result = fnChain.apply(777);

        //then
        assertThat(fnChain).isInstanceOf(LambdaHelper.FunctionCombinator.class);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void optimizeToBiFnShouldBehaveCorrectly() {
        assumeThat(LambdaHelper.OPTIMIZE)
                .withFailMessage("JDK running this test suite doesn't support optimization")
                .isTrue();

        //given
        Dummy source = new Dummy();
        source.setName("someName");
        BiFunction<Dummy, Dummy, Dummy> setNameFn = LambdaHelper.toBiFunction(Dummy::getName, Dummy::setName);

        //when
        Dummy target = new Dummy();
        Dummy result = setNameFn.apply(source, target);

        //then
        assertThat(setNameFn).isNotInstanceOf(LambdaHelper.SetterBiFn.class);
        assertThat(result).isEqualTo(target);
        assertThat(result.getName()).isEqualTo(source.getName());
    }

    @Test
    public void normalToBiFnShouldBehaveCorrectly() {
        LambdaHelper.OPTIMIZE = false;

        //given
        Dummy source = new Dummy();
        source.setName("someName");
        BiFunction<Dummy, Dummy, Dummy> setNameFn = LambdaHelper.toBiFunction(Dummy::getName, Dummy::setName);

        //when
        Dummy target = new Dummy();
        Dummy result = setNameFn.apply(source, target);

        //then
        assertThat(setNameFn).isInstanceOf(LambdaHelper.SetterBiFn.class);
        assertThat(result).isEqualTo(target);
        assertThat(result.getName()).isEqualTo(source.getName());
    }


    @Test
    public void optimizedBiFunctionAndThenShouldBehaveCorrectly() {
        assumeThat(LambdaHelper.OPTIMIZE)
                .withFailMessage("JDK running this test suite doesn't support optimization")
                .isTrue();

        //given
        Dummy source = new Dummy();
        source.setName("someName");
        BiFunction<Dummy, Dummy, Dummy> setAndDuplicateName = LambdaHelper.andThen(
                (in, out) -> {
                    out.setName(in.getName());
                    return out;
                },
                (in, out) -> {
                    out.setName(out.getName() + in.getName());
                    return out;
                }
        );

        //when
        Dummy target = new Dummy();
        Dummy result = setAndDuplicateName.apply(source, target);

        //then
        assertThat(setAndDuplicateName).isNotInstanceOf(LambdaHelper.BiFunctionCombinator.class);
        assertThat(result).isEqualTo(target);
        assertThat(result.getName()).isEqualTo(source.getName() + source.getName());
    }

    @Test
    public void normalBiFunctionAndThenShouldBehaveCorrectly() {
        LambdaHelper.OPTIMIZE = false;

        //given
        Dummy source = new Dummy();
        source.setName("someName");
        BiFunction<Dummy, Dummy, Dummy> setAndDuplicateName = LambdaHelper.andThen(
                (in, out) -> {
                    out.setName(in.getName());
                    return out;
                },
                (in, out) -> {
                    out.setName(out.getName() + in.getName());
                    return out;
                }
        );

        //when
        Dummy target = new Dummy();
        Dummy result = setAndDuplicateName.apply(source, target);

        //then
        assertThat(setAndDuplicateName).isInstanceOf(LambdaHelper.BiFunctionCombinator.class);
        assertThat(result).isEqualTo(target);
        assertThat(result.getName()).isEqualTo(source.getName() + source.getName());
    }

    private static class Dummy {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}