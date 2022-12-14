package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import java.util.*


val accessToken: String = System.getenv("JAICP_API_TOKEN") ?: Properties().run {
    load(CailaNLUSettings::class.java.getResourceAsStream("/jaicp.properties"))
    getProperty("apiToken")
}

data class Update(
    val message: Message?
)

data class Message(
    val text: String?
)

private val cailaNLUSettings = CailaNLUSettings(
    accessToken = accessToken
)


val HelloWorldScenario = Scenario {
    val dbs = DataBaseController()
    dbs.initializeDatabase()
    state("start") {
        activators {
            regex("/start")
        }

        action {
            reactions.say("Начать тестирование?")
            reactions.buttons(" Да ", " Нет ")
        }
    }

    state("firstQ") {
        activators {
            regex(" Да ")
        }
        action {
            dbs.id = 1
            dbs.correctScore = 0
            reactions.say(dbs.getQuestion().toString())
            reactions.buttons(
                dbs.getChoiseAnswer().split(", ")[0],
                dbs.getChoiseAnswer().split(", ")[1],
                dbs.getChoiseAnswer().split(", ")[2],
                dbs.getChoiseAnswer().split(", ")[3]
            )

        }
    }

    state("testing") {
        activators {
            catchAll()
        }
        action {
            val telegramRequest = request.telegram

            if (telegramRequest != null) {
                reactions.say(dbs.checkAns(telegramRequest.input.toString()).toString())
            }
            if (dbs.id == 20) {
                reactions.say("Количество верных ответ: " + dbs.correctScore)
                if (dbs.correctScore < 5) {
                    reactions.say("Ваш уровень: начинающий")
                } else if (dbs.correctScore in 5..10) {
                    reactions.say("Ваш уровень: средний ")
                } else if (dbs.correctScore in 11..15) {
                    reactions.say("Ваш уровень: выше среднего")
                } else if (dbs.correctScore in 16..20) {
                    reactions.say("Ваш уровень: наивысший")
                }
                reactions.say("Начать тестирование заново?")
                reactions.buttons(" Да ", " Нет ")
            }
            dbs.id++
            reactions.say(dbs.getQuestion().toString())
            reactions.buttons(
                dbs.getChoiseAnswer().split(", ")[0],
                dbs.getChoiseAnswer().split(", ")[1],
                dbs.getChoiseAnswer().split(", ")[2],
                dbs.getChoiseAnswer().split(", ")[3]
            )
        }
    }

    state("bye") {
        activators {
            regex(" Нет ")
        }
        action {
            reactions.say("До свидания!")
        }
    }
}


val helloWorldBot = BotEngine(
    scenario = HelloWorldScenario, activators = arrayOf(
        RegexActivator, CatchAllActivator
    )
)
