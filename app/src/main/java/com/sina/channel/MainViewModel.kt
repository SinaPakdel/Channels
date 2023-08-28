package com.sina.channel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var channel = Channel<Language>()


    private var channelProducer: ReceiveChannel<Language> = Channel()


    init {
        viewModelScope.launch {
            channel.send(Language.JAVA)
            channel.send(Language.KOTLIN)
            channel.send(Language.PYTHON)

            /**
             * if we want to close and terminate the channel
             */
            channel.close()

            /**
             * produce channel act like simple channel , but different is that we don't have to
             * worry about closing channel after all events send,
             * it's automatically closed by itself
             */


            channelProducer = produce {
                send(Language.JAVA)
                send(Language.KOTLIN)
                send(Language.PYTHON)
            }

            /**
             * Channels Types:
             * 1-BUFFERED
             * 2-CONFLATED
             * 3-RENDEZVOUS
             * 4-UNLIMITED
             */

            /**
             * capacity is just about to handle 2 send function for the first time,once
             * then, after that
             * producer remove first send element to create more space for another send
             * until all send events will be finished
             */
            channelProducer = produce(capacity = 2) {
                send(Language.JAVA)
                send(Language.KOTLIN)
                send(Language.PYTHON)
                send(Language.PYTHON)
                send(Language.PYTHON)
            }

        }

        viewModelScope.launch {
            Log.e("MainViewModel", "is Closed: ${channel.isClosedForReceive}") // false

            /**
             * channel.received() trigger only once
             */
            Log.e("MainViewModel", "Received: ${channel.receive()}")
            /**
             * in this time we get another channel from launch above
             */
            Log.e("MainViewModel", "Received: ${channel.receive()}")

            Log.e("MainViewModel", "is Closed: ${channel.isClosedForReceive}") // true

        }


        viewModelScope.launch {
            /**
             * in this way we can consume each element in this channel we have, so we should do this for
             * unknowing how many send we have
             */
            channel.consumeEach {
                Log.e("MainViewModel", "${it.name}: ")
            }
        }


        viewModelScope.launch {
            Log.e("MainViewModel", "is Closed: ${channel.isClosedForReceive}") // false

            channelProducer.consumeEach {
                Log.e("MainViewModel", "${it.name}: ")
            }

            Log.e("MainViewModel", "is Closed: ${channel.isClosedForReceive}") // false

        }


        /**
         * using channel types
         */
        viewModelScope.launch {
            Log.e("CHANNEL TYPES", "${channel.receive()}: ")
            delay(3000)
            Log.e("CHANNEL TYPES", "-------------")

            Log.e("CHANNEL TYPES", "${channel.receive()}: ")
            delay(3000)
            Log.e("CHANNEL TYPES", "-------------")

            Log.e("CHANNEL TYPES", "${channel.receive()}: ")
            delay(3000)
            Log.e("CHANNEL TYPES", "-------------")

            Log.e("CHANNEL TYPES", "${channel.receive()}: ")
            delay(3000)
            Log.e("CHANNEL TYPES", "-------------")

        }
    }
}


enum class Language {
    KOTLIN, JAVA, PYTHON
}