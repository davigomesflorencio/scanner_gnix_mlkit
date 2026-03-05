package com.davi.dev.scannermlkit.presentation.screens.legal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyTermsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = """
                    Your privacy is important to us. It is Scanner MLKit's policy to respect your privacy regarding any information we may collect from you across our website, and other sites we own and operate.

                    We only ask for personal information when we truly need it to provide a service to you. We collect it by fair and lawful means, with your knowledge and consent. We also let you know why we’re collecting it and how it will be used.

                    We only retain collected information for as long as necessary to provide you with your requested service. What data we store, we’ll protect within commercially acceptable means to prevent loss and theft, as well as unauthorized access, disclosure, copying, use or modification.

                    We don’t share any personally identifying information publicly or with third-parties, except when required to by law.

                    Our website may link to external sites that are not operated by us. Please be aware that we have no control over the content and practices of these sites, and cannot accept responsibility or liability for their respective privacy policies.

                    You are free to refuse our request for your personal information, with the understanding that we may be unable to provide you with some of your desired services.

                    Your continued use of our website will be regarded as acceptance of our practices around privacy and personal information. If you have any questions about how we handle user data and personal information, feel free to contact us.

                    This policy is effective as of 1 January 2024.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Terms of Use",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = """
                    By accessing and using the Scanner MLKit mobile application (the "App"), you agree to be bound by these Terms of Use. If you do not agree to these Terms of Use, you may not access or use the App.

                    1. Use of the App\n\t\t\t\t\t\t\tYou must be at least 13 years old to use the App. You are responsible for maintaining the confidentiality of your account and password and for restricting access to your device. You agree to accept responsibility for all activities that occur under your account or password.

                    2. User Content\n\t\t\t\t\t\t\tYou retain all rights to any content you submit, post, or display on or through the App ("User Content"). By submitting, posting, or displaying User Content, you grant Scanner MLKit a worldwide, non-exclusive, royalty-free license (with the right to sublicense) to use, copy, reproduce, process, adapt, modify, publish, transmit, display, and distribute such User Content in any and all media or distribution methods.

                    3. Prohibited Conduct\n\t\t\t\t\t\t\tYou agree not to engage in any of the following prohibited activities: (a) copying, distributing, or disclosing any part of the App in any medium; (b) using any automated system, including without limitation "robots," "spiders," "offline readers," etc., to access the App in a manner that sends more request messages to the Scanner MLKit servers than a human can reasonably produce in the same period by using a conventional on-line web browser; (c) transmitting spam, chain letters, or other unsolicited email; (d) attempting to interfere with, compromise the system integrity or security or decipher any transmissions to or from the servers running the App.

                    4. Termination\n\t\t\t\t\t\t\tScanner MLKit may terminate or suspend your access to the App immediately, without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms of Use.

                    5. Disclaimers\n\t\t\t\t\t\t\tThe App is provided on an "as is" and "as available" basis. Use of the App is at your own risk. To the maximum extent permitted by applicable law, the App is provided without warranties of any kind, whether express or implied, including, but not limited to, implied warranties of merchantability, fitness for a particular purpose, or non-infringement.

                    6. Governing Law\n\t\t\t\t\t\t\tThese Terms of Use shall be governed by the laws of Brazil, without respect to its conflict of laws principles.

                    7. Changes to the Terms of Use\n\t\t\t\t\t\t\tScanner MLKit reserves the right to modify these Terms of Use at any time. Your continued use of the App after any such changes constitutes your acceptance of the new Terms of Use.

                    Contact Us: If you have any questions about these Terms, please contact us at support@scannermlkit.com.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}