package io.horizontalsystems.ethereumkit.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public final class InternalBouncyCastleProvider {

    private static class Holder {
        private static final java.security.Provider INSTANCE;

        static {
            java.security.Provider p = java.security.Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);

            INSTANCE = (p != null) ? p : new org.bouncycastle.jce.provider.BouncyCastleProvider();

            INSTANCE.put("MessageDigest.ETH-KECCAK-256", "io.horizontalsystems.ethereumkit.crypto.digest.Keccak256");
            INSTANCE.put("MessageDigest.ETH-KECCAK-512", "io.horizontalsystems.ethereumkit.crypto.digest.Keccak512");
        }
    }

    public static java.security.Provider getInstance() {
        return io.horizontalsystems.ethereumkit.crypto.InternalBouncyCastleProvider.Holder.INSTANCE;
    }
}
