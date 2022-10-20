package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import java.util.*

var correctScore: Int = 0

val accessToken: String = System.getenv("JAICP_API_TOKEN") ?: Properties().run {
    load(CailaNLUSettings::class.java.getResourceAsStream("/jaicp.properties"))
    getProperty("apiToken")
}

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
            reactions.buttons("Да", "Нет")
        }
    }

    state("test") {
        activators {
            regex("Да")
        }
        action {
            reactions.say(dbs.getQuestion().toString())
            reactions.buttons(
                dbs.getChoiseAnswer()[0].split(",")[0],
                dbs.getChoiseAnswer()[1].split(",")[1],
                dbs.getChoiseAnswer()[2].split(",")[2],
                dbs.getChoiseAnswer()[3].split(",")[3]

            )
        }
    }
    state("testing") {
        activators {
            catchAll()
        }
        action {
            reactions.say(dbs.checkAns(request.telegram?.message.toString()).toString())
            dbs.id++
        }
    }

    state("bye") {
        activators {
            regex("Нет")
        }
        action {
            reactions.say("До свидания!")
        }
    }
}


val helloWorldBot = BotEngine(
    scenario = HelloWorldScenario,
    activators = arrayOf(
        RegexActivator,
        CatchAllActivator
    )
)
