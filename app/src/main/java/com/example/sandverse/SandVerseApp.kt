package com.example.sandverse

import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.example.sandverse.services.NavigatorHolder
import com.example.sandverse.services.NetServiceRegistrator
import com.example.sandverse.services.NetServicesDiscoverer
import com.example.sandverse.services.PermissionManager
import com.example.sandverse.services.couchbase.WSListener
import com.example.sandverse.services.couchbase.WSReplicator
import com.example.sandverse.services.wifip2p.WifiDirectBroadcastReceiver
import com.example.sandverse.services.wifip2p.WifiP2pConnectionHandler
import com.example.sandverse.viewmodels.WifiVM
import com.example.sandverse.viewmodels.MainVM
import com.example.sandverse.viewmodels.SyncDataVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class SandVerseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val ctx = module { factory { applicationContext } }

        val coroutine = module {
            single { CoroutineScope(Dispatchers.IO) }
        }

        val navigator = module {
            single { NavigatorHolder() }
        }

        val wifiP2pManager = module {
            single {
                Log.d("Koin", "Initializing WifiP2pManager...")
                androidContext().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
            }
            Log.d("Koin", "Wifi P2P Manager - initialization Success!!")
        }

        val wifiP2pChannel = module {
            single<WifiP2pManager.Channel> {
                Log.d("Koin", "Initializing WifiP2pManager.Channel...")
                get<WifiP2pManager>().initialize(
                    androidContext(),
                    androidContext().mainLooper,
                    null
                )
            }
            Log.d("Koin", "Wifi P2P Manager Channel- initialization Success!!")
        }

        val broadcastReceiver = module {
            single {
                Log.d("Koin", "Initializing Wifi P2P Broadcast Receiver...")
                WifiDirectBroadcastReceiver(get())// wifiP2pManager
            }
            Log.d("Koin", "Wifi P2P Broadcast Receiver - initialization Success!!")
        }

        val connectionHandler = module {
            single { WifiP2pConnectionHandler(
                get(), // WifiP2pManager
                get() // .Channel
            ) }
        }

        val serviceManager = module {
            single {
                getSystemService(Context.NSD_SERVICE) as NsdManager
            }
            single { NetServiceRegistrator(get()) }
            single { NetServicesDiscoverer(get()) }
        }

        val viewModels = module {
            viewModel { MainVM() }
            viewModel { SyncDataVM() }

            viewModel {
                WifiVM(
                    get(),  // wifiP2pManager
                    get(),  // wifiP2pManager.Channel
                    get()   // broadcast Receiver
                )
            }
        }



        val syncData = module {
            single { PermissionManager() }
            single { serviceManager }
            single { WSListener() }
            single { WSReplicator(get()) }
        }

        startKoin {
            androidContext(this@SandVerseApp)
            modules(
                ctx,
                coroutine,
                navigator,
                wifiP2pManager,
                wifiP2pChannel,
                broadcastReceiver,
                connectionHandler,
                serviceManager,
                viewModels,
                syncData
            )
        }
    }
}
