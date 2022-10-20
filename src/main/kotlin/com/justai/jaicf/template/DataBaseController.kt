package com.justai.jaicf.template

import oracle.jdbc.pool.OracleDataSource
import java.sql.*


class DataBaseController {
    private lateinit var conn: Connection
    private val pstmt: PreparedStatement? = null
    var statement: Statement? = null
    var id: Int = 1
    var correctScore: Int = 0


    fun initializeDatabase() { // Инициализация и подключение бд
        val username = "KotlinTest"
        val password = "1234"
        try {
            var ods = OracleDataSource()
            ods = OracleDataSource()
            ods.setURL("jdbc:oracle:thin:$username/$password@localhost:1521/orc")
            conn = ods.getConnection()
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
            println("Connection success")
        } catch (e: SQLException) {
            println(e)
        }
    }


    @Throws(SQLException::class)
    fun getQuestion(): String? { // Получение вопроса
        val query = "SELECT QUESTION FROM TEST WHERE ID =$id"
        var resultSet = statement!!.executeQuery(query)
        resultSet = statement!!.executeQuery(query)
        resultSet.next()
        return resultSet.getString(1)
    }


    @JvmName("getChoiseAnswer1")
    @Throws(SQLException::class)
    fun getChoiseAnswer(): String { // Получение выбора ответа
        var choiseAnswer: String = ""
        val query = "SELECT CHOISE FROM TEST WHERE ID =$id"
        var resultSet = statement!!.executeQuery(query)
        resultSet = statement!!.executeQuery(query)
        resultSet.next()
        choiseAnswer = resultSet.getString(1)
        return choiseAnswer
    }

    @Throws(SQLException::class)
    fun checkAns(answer: String): String? { // Проверка правильного ответа
        val query = "SELECT TRUEANSWER FROM TEST WHERE ID =$id"
        var resultSet = statement!!.executeQuery(query)
        resultSet = statement!!.executeQuery(query)
        resultSet.next()
        return if (resultSet.getString(1).equals(answer)) {
            correctScore++
            "Верно"
        } else
            "Неверно"
    }
}