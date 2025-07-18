/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package supersymmetry.api.recycling.toposort;

import com.google.common.base.Preconditions;
import com.google.common.graph.Graph;
import com.google.common.graph.ValueGraph;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// Provides a topological sort algorithm.
///
/// While this algorithm is used for mod loading in forge, it can be
/// utilized in other fashions, e.g. topology-based registry loading, prioritization
/// for renderers, and even mod module loading.
///
/// [CleanroomLoader](cleanroommc.com) is using a newer version of [Guava](guava.dev) library,
/// where the [ValueGraph] no longer inherits from [Graph].
@SuppressWarnings("UnstableApiUsage")
public final class TopologicalSort {

    /// A breath-first-search based topological sort.
    ///
    /// Compared to the depth-first-search version, it does not reverse the graph
    /// and supports custom secondary ordering specified by a comparator. It also utilizes the
    /// recently introduced Guava Graph API, which is more straightforward than the old directed
    /// graph.
    ///
    /// The graph to sort must be directed, must not allow self loops, and must not contain
    /// cycles. [IllegalArgumentException] will be thrown otherwise.
    ///
    /// When `null` is used for the comparator and multiple nodes have no
    /// prerequisites, the order depends on the iteration order of the set returned by the
    /// [#successors(Object)] call, which is random by default.
    ///
    /// Given the number of edges `E` and the number of vertexes `V`,
    /// the time complexity of a sort without a secondary comparator is `O(E + V)`.
    /// With a secondary comparator of time complexity `O(T)`, the overall time
    /// complexity would be `O(E + TV log(V))`. As a result, the comparator should
    /// be as efficient as possible.
    ///
    /// Examples of topological sort usage can be found in Forge test code.
    ///
    /// @param graph      the graph to sort
    /// @param comparator the secondary comparator, may be null
    /// @param <T>        the node type of the graph
    /// @return the ordered nodes from the graph
    /// @throws IllegalArgumentException if the graph is undirected or allows self loops
    /// @throws CyclePresentException    if the graph contains cycles
    public static <T> List<T> topologicalSort(ValueGraph<T, ?> graph, @Nullable Comparator<? super T> comparator) throws IllegalArgumentException {
        Preconditions.checkArgument(graph.isDirected(), "Cannot topologically sort an undirected graph!");
        Preconditions.checkArgument(!graph.allowsSelfLoops(), "Cannot topologically sort a graph with self loops!");

        final Queue<T> queue = comparator == null ? new ArrayDeque<>() : new PriorityQueue<>(comparator);
        final Map<T, Integer> degrees = new HashMap<>();
        final List<T> results = new ArrayList<>();

        for (final T node : graph.nodes()) {
            final int degree = graph.inDegree(node);
            if (degree == 0) {
                queue.add(node);
            } else {
                degrees.put(node, degree);
            }
        }

        while (!queue.isEmpty()) {
            final T current = queue.remove();
            results.add(current);
            for (final T successor : graph.successors(current)) {
                final int updated = degrees.compute(successor, (node, degree) -> Objects.requireNonNull(degree, () -> "Invalid degree present for " + node) - 1);
                if (updated == 0) {
                    queue.add(successor);
                    degrees.remove(successor);
                }
            }
        }

        if (!degrees.isEmpty()) {
            Set<Set<T>> components = new StronglyConnectedComponentDetector<>(graph).getComponents();
            components.removeIf(set -> set.size() < 2);
            throwCyclePresentException(components);
        }

        return results;
    }

    @SuppressWarnings("unchecked") /// for unchecked annotation
    private static <T> void throwCyclePresentException(Set<Set<T>> components) {
        throw new CyclePresentException((Set<Set<?>>) (Set<?>) components);
    }
}
