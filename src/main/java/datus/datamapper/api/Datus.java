package datus.datamapper.api;

import datus.datamapper.functions.Fn1;
import datus.datamapper.functions.Fn2;
import datus.datamapper.functions.Fn3;
import datus.datamapper.functions.Fn4;
import datus.datamapper.functions.Fn5;
import datus.datamapper.functions.Fn6;
import datus.datamapper.functions.Fn7;
import datus.datamapper.functions.Fn8;
import datus.datamapper.immutable.ConstructorBuilder1;
import datus.datamapper.immutable.ConstructorBuilder2;
import datus.datamapper.immutable.ConstructorBuilder3;
import datus.datamapper.immutable.ConstructorBuilder4;
import datus.datamapper.immutable.ConstructorBuilder5;
import datus.datamapper.immutable.ConstructorBuilder6;
import datus.datamapper.immutable.ConstructorBuilder7;
import datus.datamapper.immutable.ConstructorBuilder8;
import datus.datamapper.mutable.MutableMappingBuilder;

import java.util.function.Supplier;

/**
 * Main (helper)class to start building a mapping process between two types.
 * <p>
 * The main benefit of using this class over directly instantiating its functions return values is a better
 * type inference as most of the datus api (especially the immutable) is currently uninferable for the java compiler
 * (diamond does not work)
 *
 * @param <In>  the input type
 * @param <Out> the output type
 */
public class Datus<In, Out> {
    /**
     * Constructs a typed context for a mapping process definition for the given input and output types.
     *
     * @param inType  the input type (used to help java infer the arguments for the coming function calls)
     * @param outType the output type (used to help java infer the arguments for the coming function calls)
     * @param <In>    the input type parameter
     * @param <Out>   the output type parameter
     * @return
     */
    public static <In, Out> Datus<In, Out> start(Class<In> inType, Class<Out> outType) {
        return new Datus<>();
    }

    /**
     * Starts a mutable mapping process definition using the given supplier to instantiate the output type.
     *
     * @param supplier a supplier to instantiate the output type when converting an input type
     * @return a mutable mapping builder
     */
    public MutableMappingBuilder<In, Out> mutable(Supplier<Out> supplier) {
        return new MutableMappingBuilder<>(supplier);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @return an immutable mapping builder
     */
    public <A>
    ConstructorBuilder1<In, A, Out> immutable(Fn1<A, Out> constructor) {
        return new ConstructorBuilder1<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @return an immutable mapping builder
     */
    public <A, B>
    ConstructorBuilder2<In, A, B, Out> immutable(Fn2<A, B, Out> constructor) {
        return new ConstructorBuilder2<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C>
    ConstructorBuilder3<In, A, B, C, Out> immutable(Fn3<A, B, C, Out> constructor) {
        return new ConstructorBuilder3<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @param <D>         the constructors 4th parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C, D>
    ConstructorBuilder4<In, A, B, C, D, Out> immutable(Fn4<A, B, C, D, Out> constructor) {
        return new ConstructorBuilder4<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @param <D>         the constructors 4th parameters type
     * @param <E>         the constructors 5th parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C, D, E>
    ConstructorBuilder5<In, A, B, C, D, E, Out> immutable(Fn5<A, B, C, D, E, Out> constructor) {
        return new ConstructorBuilder5<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @param <D>         the constructors 4th parameters type
     * @param <E>         the constructors 5th parameters type
     * @param <F>         the constructors 6th parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C, D, E, F>
    ConstructorBuilder6<In, A, B, C, D, E, F, Out> immutable(Fn6<A, B, C, D, E, F, Out> constructor) {
        return new ConstructorBuilder6<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @param <D>         the constructors 4th parameters type
     * @param <E>         the constructors 5th parameters type
     * @param <F>         the constructors 6th parameters type
     * @param <G>         the constructors 7th parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C, D, E, F, G>
    ConstructorBuilder7<In, A, B, C, D, E, F, G, Out> immutable(Fn7<A, B, C, D, E, F, G, Out> constructor) {
        return new ConstructorBuilder7<>(constructor);
    }

    /**
     * Starts an immutable mapping process definition using the given constructor of the output type.
     *
     * @param constructor the constructor to use
     * @param <A>         the constructors 1st parameters type
     * @param <B>         the constructors 2nd parameters type
     * @param <C>         the constructors 3rd parameters type
     * @param <D>         the constructors 4th parameters type
     * @param <E>         the constructors 5th parameters type
     * @param <F>         the constructors 6th parameters type
     * @param <G>         the constructors 7th parameters type
     * @param <H>         the constructors 8th parameters type
     * @return an immutable mapping builder
     */
    public <A, B, C, D, E, F, G, H>
    ConstructorBuilder8<In, A, B, C, D, E, F, G, H, Out> immutable(Fn8<A, B, C, D, E, F, G, H, Out> constructor) {
        return new ConstructorBuilder8<>(constructor);
    }

    private Datus() {
    }
}
