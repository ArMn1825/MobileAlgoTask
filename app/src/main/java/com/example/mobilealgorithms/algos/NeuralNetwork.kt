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
        this.size = size.copy()
        matrix = Array(size.first) { DoubleArray(size.second) {
            if (fillRandom) Random.nextDouble(-0.5, 0.5) else 0.0
        } }
    }
    companion object {
        fun copy(m: Matrix) : Matrix {
            val result = Matrix(m.size)
            for (i in 0 until m.size.first) {
                for (j in 0 until m.size.second) {
                    result.matrix[i][j] = m.matrix[i][j]
                }
            }
            return result
        }
        fun transpose(m: Matrix): Matrix {
            val result = Matrix(Pair(m.size.second, m.size.first))
            for (i in 0 until m.size.first) {
                for (j in 0 until m.size.second) {
                    result.matrix[j][i] = m.matrix[i][j]
                }
            }
            return result
        }
        fun add(m1: Matrix, m2: Matrix) : Matrix {
            if (m2.size.first != m1.size.first || m2.size.second != m1.size.second) {
                throw Exception("Incorrect matrix size")
            }
            val result = Matrix(m1.size)
            for (i in 0 until m1.size.first) {
                for (j in 0 until m1.size.second) {
                    result.matrix[i][j] = m1.matrix[i][j] + m2.matrix[i][j]
                }
            }
            return result
        }
        fun mul(m1: Matrix, m2: Matrix) : Matrix {
            /* matrix1 * matrix2 */
            if (m1.size.second != m2.size.first) {
                throw Exception("Incorrect matrix size")
            }
            val result = Matrix(Pair(m1.size.first, m2.size.second))
            for (i in 0 until m1.size.first) {
                for (j in 0 until m2.size.second) {
                    for (k in 0 until m1.size.second) {
                        result.matrix[i][j] += m1.matrix[i][k] * m2.matrix[k][j]
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

private data class NNParameters(val values: Matrix, val gradient: Matrix) {}
private abstract class NNModule {
    abstract fun forward(inputData: Matrix) : Matrix
    abstract fun backward(outputGrad: Matrix) : Matrix
    abstract fun zeroGrad()
    abstract fun parameters() : List<NNParameters>
}

private class Linear (height: Int, width: Int) : NNModule() {
    private val weights = Matrix(Pair(height, width), true)
    private var weightsGrad = Matrix(weights.getSize(), false)
    private val bias = Matrix(Pair(1, width), true)
    private var biasGrad = Matrix(bias.getSize(), false)
    private var inputData: Matrix? = null
    override fun forward(inputData: Matrix) : Matrix {
        this.inputData = inputData
        return Matrix.add(Matrix.mul(inputData, Matrix.transpose(weights)),
                          bias)
        /* y = x * W^T + b */
    }
    override fun parameters(): List<NNParameters> {
        return listOf(NNParameters(weights, weightsGrad),
                      NNParameters(bias, biasGrad))
    }
    override fun zeroGrad() {
        for (line in weightsGrad.matrix) {
            line.fill(0.0)
        }
        biasGrad.matrix[0].fill(0.0)
    }
    override fun backward(outputGrad: Matrix): Matrix {
        val inputGrad = Matrix.mul(outputGrad, weights)
        /* dL/dx = dL/dy * dy/dx, dy/dx = W */
        val weightsGrad = Matrix.mul(Matrix.transpose(outputGrad), inputData!!)
        /* dL/dW = dL/dy * dy/dW, dy/dW = x */
        val biasGrad = Matrix(bias.getSize(), false)
        /* dL/db = dL/dy * dy/db, dy/db = 1 */
        for (i in 0 until outputGrad.getSize().first) {
            for (j in 0 until outputGrad.getSize().second) {
                biasGrad.matrix[0][j] += outputGrad.matrix[i][j]
            }
        }
        this.weightsGrad = Matrix.add(this.weightsGrad, weightsGrad)
        this.biasGrad = Matrix.add(this.biasGrad, biasGrad)
        return inputGrad
    }
}