package com.example.mobilealgorithms.algos

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.sqrt

class AStar {
    enum class HeuristicType { MANHATTAN, EUCLIDEAN };
    data class AStarResult(
        val path: List<Pair<Int, Int>>?,
        val orderOfVisit: List<Pair<Int, Int>>?
    )
    private val plot: Array<IntArray>;
    private val allowDiagonal: Boolean;
    private val heuristicType: HeuristicType;
    private val width: Int;
    private val height: Int;

    constructor(plot: Array<IntArray>, allowDiagonal: Boolean = true,
                heuristicType: HeuristicType = HeuristicType.EUCLIDEAN) {
        this.plot = plot;
        this.allowDiagonal = allowDiagonal;
        this.heuristicType = heuristicType;
        this.width = plot[0].size;
        this.height = plot.size;
    }
    private fun heuristic(point1: Pair<Int, Int>, point2: Pair<Int, Int>): Double {
        val dx = abs(point1.first - point2.first);
        val dy = abs(point1.second - point2.second);
        if (heuristicType == HeuristicType.MANHATTAN) {
            return (dx + dy).toDouble();
        } else if (heuristicType == HeuristicType.EUCLIDEAN) {
            return sqrt((dx * dx + dy * dy).toDouble());
        } else {
            return 0.0;
        }
    }
    private fun cost(point0: Pair<Int, Int>, point1: Pair<Int, Int>): Double {
        /* points0 and points1 must be neighbours, else result is incorrect */
        val dx = abs(point0.first - point1.first);
        val dy = abs(point0.second - point1.second);
        return if (dx + dy == 2) sqrt(2.0) else 1.0;
    }
    private fun getNeighbours(point: Pair<Int, Int>): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>();
        val baseDirections = listOf(Pair(0, -1), Pair(0, 1), Pair(-1, 0), Pair(1, 0));
        val diagDirections = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1));
        for (dir in baseDirections) {
            val newX = point.first + dir.first;
            val newY = point.second + dir.second;
            if (newX >= 0 && newX < height && newY >= 0 && newY < width && plot[newX][newY] == 0) {
                neighbors.add(Pair(newX, newY));
            }
        }
        if (!allowDiagonal) {
            return neighbors;
        }
        for (dir in diagDirections) {
            val newX = point.first + dir.first;
            val newY = point.second + dir.second;
            if (newX >= 0 && newX < height && newY >= 0 && newY < width && plot[newX][newY] == 0) {
                neighbors.add(Pair(newX, newY));
            }
        }
        return neighbors;
    }
    private fun restorePath(cameFrom: Map<Pair<Int, Int>, Pair<Int, Int>>,
                            current: Pair<Int, Int>): List<Pair<Int, Int>>
    {
        val path = mutableListOf<Pair<Int, Int>>();
        var node: Pair<Int, Int>? = current;
        while (node != null) {
            path.add(node);
            node = cameFrom[node];
        }
        return path.reversed();
    }

    public fun findPath(start: Pair<Int, Int>, end: Pair<Int, Int>): AStarResult {
        if (plot[start.first][start.second] != 0 || plot[end.first][end.second] != 0) {
            return AStarResult(null, null);
        }
        val pathDist = Array(height) { DoubleArray(width) { Double.POSITIVE_INFINITY } };
        val pointQueue = PriorityQueue<Pair<Int, Int>>(compareBy {
            pathDist[it.first][it.second] + heuristic(it, end);
        });
        val prevPoint = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>();
        val orderOfVisit = mutableListOf<Pair<Int, Int>>();

        pathDist[start.first][start.second] = 0.0;
        pointQueue.add(start);
        while (pointQueue.isNotEmpty()) {
            val current = pointQueue.poll();
            orderOfVisit.add(current);
            if (current == end) {
                val path = restorePath(prevPoint, current);
                return AStarResult(path, orderOfVisit);
            }
            for (it in getNeighbours(current)) {
                val newDist = pathDist[current.first][current.second] + cost(current, it);
                if (newDist < pathDist[it.first][it.second]) {
                    prevPoint[it] = current;
                    pathDist[it.first][it.second] = newDist;
                    pointQueue.add(it);
                }
            }
        }
        return AStarResult(null, orderOfVisit);
    }
}
