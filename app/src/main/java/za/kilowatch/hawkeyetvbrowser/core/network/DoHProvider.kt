package za.kilowatch.hawkeyetvbrowser.core.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoHProvider @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    enum class ProviderOption(val id: String, val displayName: String, val url: String?, val bootstrapIps: List<InetAddress>) {
        OFF("OFF", "Off (System Default)", null, emptyList()),
        CLOUDFLARE(
            "CLOUDFLARE",
            "Cloudflare (1.1.1.1)",
            "https://cloudflare-dns.com/dns-query",
            listOf(
                InetAddress.getByName("1.1.1.1"),
                InetAddress.getByName("1.0.0.1")
            )
        ),
        GOOGLE(
            "GOOGLE",
            "Google (8.8.8.8)",
            "https://dns.google/dns-query",
            listOf(
                InetAddress.getByName("8.8.8.8"),
                InetAddress.getByName("8.8.4.4")
            )
        ),
        ADGUARD(
            "ADGUARD",
            "AdGuard DNS",
            "https://dns.adguard-dns.com/dns-query",
            listOf(
                InetAddress.getByName("94.140.14.14"),
                InetAddress.getByName("94.140.15.15")
            )
        ),
        CUSTOM("CUSTOM", "Custom DoH Endpoint", null, emptyList())
    }

    fun configureDns(builder: OkHttpClient.Builder, bootstrapClient: OkHttpClient) {
        val providerId = settingsRepository.getDohProvider()
        if (providerId == "OFF") return

        val option = ProviderOption.values().find { it.id == providerId } ?: ProviderOption.OFF
        val dohUrl = if (option == ProviderOption.CUSTOM) {
            settingsRepository.getDohCustomUrl().ifBlank { return }
        } else {
            option.url ?: return
        }

        try {
            val dns = DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url(dohUrl.toHttpUrl())
                .apply {
                    if (option.bootstrapIps.isNotEmpty()) {
                        bootstrapDnsHosts(option.bootstrapIps)
                    }
                }
                .build()
            builder.dns(dns)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
