package com.yeungs.decisionengine.util;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 15:19
 * @description:
 * 参考：https://github.com/apache/bahir-flink/blob/master/flink-connector-netty/src/main/scala/org/apache/flink/streaming/connectors/netty/example/NettyUtil.scala
 */

public class NettyUtil {

    private static final Logger logger = LoggerFactory.getLogger(NettyUtil.class);

    /**
     * find local inet addresses
     */
    public static InetAddress findLocalInetAddress() throws UnknownHostException, SocketException {

        InetAddress address = InetAddress.getLocalHost();

        if (address.isAnyLocalAddress()) {
            List<NetworkInterface> activeNetworkIFs = EnumerationUtils.toList(NetworkInterface.getNetworkInterfaces());
            List<NetworkInterface> reOrderedNetworkIFs = SystemUtils.IS_OS_WINDOWS ? activeNetworkIFs : Lists.reverse(activeNetworkIFs);
            Stream<InetAddress> addrStream=reOrderedNetworkIFs.stream().flatMap(ni -> {
                List<InetAddress> inetAddresses = EnumerationUtils.toList(ni.getInetAddresses());
                return inetAddresses.stream().filter(a -> !(a.isLinkLocalAddress() || a.isLoopbackAddress()));
            });

           return addrStream.filter(a -> a instanceof Inet4Address).findFirst().orElseGet(() -> addrStream.findFirst().orElseGet(()->address));
        }

        return address;
    }

    /**
     * start service, if port is collision, retry 128 times
     * Tip: this function is copy from spark: org.apache.spark.util.Utils.scala#L2172
     * Its better way to retry unused port
     */
    public static  <T> T startServiceOnPort(Integer startPort, Function<Integer, T> startService, Integer maxRetries, String serviceName) throws Exception {

        if (startPort != 0 && (startPort < 1024 || startPort > 65536)) {
            throw new Exception("startPort should be between 1024 and 65535 (inclusive), " +
                    "or 0 for a random free port.");
        }

        String serviceString = serviceName.isEmpty() ? "" : " '$serviceName'";
        for (int offset = 0; offset < maxRetries; offset++) {
            // Do not increment port if startPort is 0, which is treated as a special port
            // If the new port wraps around, do not try a privilege port
            Integer tryPort = startPort == 0 ? startPort : ((startPort + offset - 1024) % (65536 - 1024)) + 1024;
            try {
                T result = startService.apply(tryPort);
                logger.info("Successfully started service$serviceString, result:{}", result);
                return result;
            } catch (Exception e) {
                if (isBindCollision(e)) {
                    if (offset >= maxRetries) {
                        String exceptionMessage = e.getMessage() + ": Service" + serviceString + " failed after " +
                                maxRetries + " retries! Consider explicitly setting the appropriate port for the " +
                                "service" + serviceString + " (for example spark.ui.port for SparkUI) to an available " +
                                "port or increasing spark.port.maxRetries.";
                        Exception exception = new BindException(exceptionMessage);
                        // restore original stack trace
                        exception.setStackTrace(e.getStackTrace());
                        throw exception;
                    }
                    logger.error("Service$serviceString could not bind on port {}. " +
                            "Attempting port {}.", tryPort, tryPort + 1);
                }
            }

        }

        // Should never happen
        throw new RuntimeException("Failed to start service$serviceString on port $startPort");
    }

    /**
     * send GET request to this url
     */
    public static String sendGetRequest(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            Integer code = null;
            code = con.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine = "";
                StringBuilder response = new StringBuilder();
                try {
                    while (inputLine != null) {
                        response.append(inputLine);
                        inputLine = in.readLine();
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response.toString();

            } else {
                throw new RuntimeException("GET request not worked of url: " + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return whether the exception is caused by an address-port collision when binding.
     */
    private static Boolean isBindCollision(Throwable exception) {

        if (exception instanceof BindException) {
            if (exception.getMessage() != null) {
                return true;
            }
        }
        if (exception instanceof BindException)
            return isBindCollision(exception.getCause());
        if (exception instanceof Exception)
            return isBindCollision(exception.getCause());
        return false;

    }
}
