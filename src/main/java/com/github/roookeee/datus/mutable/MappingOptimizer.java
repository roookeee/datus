package com.github.roookeee.datus.mutable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

class MappingOptimizer {

    private static final int LAST_CASE_MAPPING_COUNT = 8;

    static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> flattenAndOptimizeMappings(
            List<BiFunction<? super In, ? super Out, ? extends Out>> mappings
    ) {
        /*
            [perf]: naive .andThen()-chaining generates n-1 wrapper-functions which amount to a total of n + (n-1)
            function calls. Especially simple mappers are affected by this additional indirection (cost of function-
            calling) by as much as a 50% performance-hit.

            Special-case 1..8 functions into specialised wrapper functions which only add 1 more indirection.
            Any list containing more than 8 functions is split into sublists which follow the same optimization procedure
            while getting chained thereafter.
         */
        switch (mappings.size()) {
            case 1:
                return mappings.get(0);
            case 2:
                return optimize2Mappings(mappings);
            case 3:
                return optimize3Mappings(mappings);
            case 4:
                return optimize4Mappings(mappings);
            case 5:
                return optimize5Mappings(mappings);
            case 6:
                return optimize6Mappings(mappings);
            case 7:
                return optimize7Mappings(mappings);
            case LAST_CASE_MAPPING_COUNT:
                return optimize8Mappings(mappings);
            default:
                BiFunction<? super In, ? super Out, ? extends Out> firstEightMappings = optimize8Mappings(mappings);
                BiFunction<? super In, ? super Out, ? extends Out> remainingMappings = flattenAndOptimizeMappings(
                        mappings.subList(LAST_CASE_MAPPING_COUNT, mappings.size())
                );

                List<BiFunction<? super In, ? super Out, ? extends Out>> toOptimize = new ArrayList<>();
                toOptimize.add(firstEightMappings);
                toOptimize.add(remainingMappings);

                return optimize2Mappings(toOptimize);
        }
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize2Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);

        return (in, out) -> b.apply(in, a.apply(in, out));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize3Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);

        return (in, out) -> c.apply(in, b.apply(in, a.apply(in, out)));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize4Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);
        BiFunction<? super In, ? super Out, ? extends Out> d = remaining.get(3);

        return (in, out) -> d.apply(in, c.apply(in, b.apply(in, a.apply(in, out))));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize5Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);
        BiFunction<? super In, ? super Out, ? extends Out> d = remaining.get(3);
        BiFunction<? super In, ? super Out, ? extends Out> e = remaining.get(4);

        return (in, out) -> e.apply(in, d.apply(in, c.apply(in, b.apply(in, a.apply(in, out)))));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize6Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);
        BiFunction<? super In, ? super Out, ? extends Out> d = remaining.get(3);
        BiFunction<? super In, ? super Out, ? extends Out> e = remaining.get(4);
        BiFunction<? super In, ? super Out, ? extends Out> f = remaining.get(5);

        return (in, out) -> f.apply(in, e.apply(in, d.apply(in, c.apply(in, b.apply(in, a.apply(in, out))))));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize7Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);
        BiFunction<? super In, ? super Out, ? extends Out> d = remaining.get(3);
        BiFunction<? super In, ? super Out, ? extends Out> e = remaining.get(4);
        BiFunction<? super In, ? super Out, ? extends Out> f = remaining.get(5);
        BiFunction<? super In, ? super Out, ? extends Out> g = remaining.get(6);

        return (in, out) -> g.apply(in, f.apply(in, e.apply(in, d.apply(in, c.apply(in, b.apply(in, a.apply(in, out)))))));
    }

    private static <In, Out> BiFunction<? super In, ? super Out, ? extends Out> optimize8Mappings(List<BiFunction<? super In, ? super Out, ? extends Out>> remaining) {
        BiFunction<? super In, ? super Out, ? extends Out> a = remaining.get(0);
        BiFunction<? super In, ? super Out, ? extends Out> b = remaining.get(1);
        BiFunction<? super In, ? super Out, ? extends Out> c = remaining.get(2);
        BiFunction<? super In, ? super Out, ? extends Out> d = remaining.get(3);
        BiFunction<? super In, ? super Out, ? extends Out> e = remaining.get(4);
        BiFunction<? super In, ? super Out, ? extends Out> f = remaining.get(5);
        BiFunction<? super In, ? super Out, ? extends Out> g = remaining.get(6);
        BiFunction<? super In, ? super Out, ? extends Out> h = remaining.get(7);

        return (in, out) -> h.apply(in, g.apply(in, f.apply(in, e.apply(in, d.apply(in, c.apply(in, b.apply(in, a.apply(in, out))))))));
    }

    private MappingOptimizer() {

    }
}
