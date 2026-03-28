package com.example.mobilealgorithms.algos

import kotlin.random.Random

class NeuralNetwork {
}

private class Matrix {
    public val matrix: Array<DoubleArray>
    private val size: Pair<Int, Int>
    public fun getSize(): Pair<Int, Int>  {
        return Pair(size.first, size.second)
    }
    constructor(size: Pair<Int, Int>, fillRandom: Boolean = false) {
        this.size = size.copy();
        matrix = Array(size.first) { DoubleArray(size.second) {
            if (fillRandom) Random.nextDouble(0.0, 1.0) else 0.0
        } }
    }
    companion object {
        fun copy(matrix: Matrix) : Matrix {
            val result = Matrix(matrix.size)
            for (i in 0 until matrix.size.first) {
                for (j in 0 until matrix.size.second) {
                    result.matrix[i][j] = matrix.matrix[i][j]
                }
            }
            return result
        }
        fun transpose(matrix: Matrix): Matrix {
            val result = Matrix(Pair(matrix.size.second, matrix.size.first))
            for (i in 0 until matrix.size.first) {
                for (j in 0 until matrix.size.second) {
                    result.matrix[j][i] = matrix.matrix[i][j]
                }
            }
            return result
        }
        fun add(matrix1: Matrix, matrix2: Matrix) : Matrix {
            if (matrix2.size.first != matrix1.size.first || matrix2.size.second != matrix1.size.second) {
                throw Exception("Incorrect matrix size")
            }
            val result = Matrix(matrix1.size)
            for (i in 0 until matrix1.size.first) {
                for (j in 0 until matrix1.size.second) {
                    result.matrix[i][j] = matrix1.matrix[i][j] + matrix2.matrix[i][j]
                }
            }
            return result
        }
        fun mul(matrix1: Matrix, matrix2: Matrix) : Matrix {
            /* matrix1 * matrix2 */
            if (matrix1.size.second != matrix2.size.first) {
                throw Exception("Incorrect matrix size")
            }
            val result = Matrix(Pair(matrix1.size.first, matrix2.size.second))
            for (i in 0 until matrix1.size.first) {
                for (j in 0 until matrix2.size.second) {
                    for (k in 0 until matrix1.size.second) {
                        result.matrix[i][j] += matrix1.matrix[i][k] * matrix2.matrix[k][j]
                    }
                }
            }
            return result
        }
        fun map(matrix: Matrix, function: (Double) -> Double) : Matrix {
            val result = Matrix(matrix.size)
            for (i in 0 until matrix.size.first) {
                for (j in 0 until matrix.size.second) {
                    result.matrix[i][j] = function(matrix.matrix[i][j])
                }
            }
            return result
        }
    }
}