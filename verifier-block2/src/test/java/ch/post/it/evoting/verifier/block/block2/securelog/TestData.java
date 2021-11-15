/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import java.math.BigInteger;
import java.util.Base64;

public class TestData {

	private static final Base64.Decoder DECODER = Base64.getDecoder();

	public static final String INITIAL_CHECKPOINT_LOG_LINE = "2021-10-22 12:26:16.493+0200 INFO  SecureLogAppender: - [CHECKPOINT: First line]{\"ESK\":\"FXo6o+L2ajCwKOWIhs5ozm70JlJdVpO2j9Z/7QJpJfduIy8VwG27tqPuqiRH9PrP9bsjg4pqfuySz4Sp5RVTe4r7a6m/3m5T46Yg7KH/qzIxkhNyKTzv+tW0NNnOjv4MW64WiZ8o6X8U6yxpH9mnI8kneQ8BhkFGjUVj+oNqNavMEDyt7VRG9Xa5ownxFs631bbYyz6TGaeh0zRcSabo3G2XECIQlckJgMGTj/TtsiR67XZr1lWms+9duDCRbA+VXpandPiFGaNz8qSLOj2ues2+Ho7b9WB4SP0Uba/SzcEMA9KKjCVD+CDAfa9ROM3jCHmIv2eNZE89Ka1dcdJtOQ==\",\"LS\":\"12\",\"TS\":\"1634898376493\",\"HMAC\":\"vd2rFQG3i3oNDnfKM6uD1U9ze4OUgu57tWho+B+5loI=\",\"SG\":\"clB7rtgoi6e5M9XkYf4j/1j9Jx2ZkaSSLITJ+cpUucitUTLDS9TZSrIxiRoPjkqVwDE+8CIb4rQ1iqNCHKAyr2ieRaQvksnTdgkA1SGP4Z232tn6GT3UWHga8likqPDu1jP/cyH8vFrAXJDx8NsN34243XoQ2sOZWBaBEaulKTKFMlyPkAz2+BzXF/ClWb1TTmRraIzqbe2CSILb6uQ2hIKnJCalXvTK1jH6B/dgj7ra/xe9D9dORUA2Gbe27UbhOahHnLUNO/tOGJ5INfgrbKTazTGkYWO1hiVjYJw9WNvDBZh4wzvTP6IZZW4BExjp3beUUgQ874NgtJKMxsvvIQ==\"}\n";
	public static final byte[] INITIAL_CHECKPOINT_HMAC_KEY = DECODER.decode("8mR+wTqcLglIzeeRBhwIQlCESQTHcR1O08KahXCLzFY=");
	public static final String INITIAL_CHECKPOINT_MESSAGE = "2021-10-22 12:26:16.493+0200 INFO  SecureLogAppender: - [CHECKPOINT: First line]";
	public static final String INITIAL_CHECKPOINT_METADATA = "{\"ESK\":\"FXo6o+L2ajCwKOWIhs5ozm70JlJdVpO2j9Z/7QJpJfduIy8VwG27tqPuqiRH9PrP9bsjg4pqfuySz4Sp5RVTe4r7a6m/3m5T46Yg7KH/qzIxkhNyKTzv+tW0NNnOjv4MW64WiZ8o6X8U6yxpH9mnI8kneQ8BhkFGjUVj+oNqNavMEDyt7VRG9Xa5ownxFs631bbYyz6TGaeh0zRcSabo3G2XECIQlckJgMGTj/TtsiR67XZr1lWms+9duDCRbA+VXpandPiFGaNz8qSLOj2ues2+Ho7b9WB4SP0Uba/SzcEMA9KKjCVD+CDAfa9ROM3jCHmIv2eNZE89Ka1dcdJtOQ==\",\"LS\":\"12\",\"TS\":\"1634898376493\",\"HMAC\":\"vd2rFQG3i3oNDnfKM6uD1U9ze4OUgu57tWho+B+5loI=\",\"SG\":\"clB7rtgoi6e5M9XkYf4j/1j9Jx2ZkaSSLITJ+cpUucitUTLDS9TZSrIxiRoPjkqVwDE+8CIb4rQ1iqNCHKAyr2ieRaQvksnTdgkA1SGP4Z232tn6GT3UWHga8likqPDu1jP/cyH8vFrAXJDx8NsN34243XoQ2sOZWBaBEaulKTKFMlyPkAz2+BzXF/ClWb1TTmRraIzqbe2CSILb6uQ2hIKnJCalXvTK1jH6B/dgj7ra/xe9D9dORUA2Gbe27UbhOahHnLUNO/tOGJ5INfgrbKTazTGkYWO1hiVjYJw9WNvDBZh4wzvTP6IZZW4BExjp3beUUgQ874NgtJKMxsvvIQ==\"}\n";
	public static final byte[] INITIAL_CHECKPOINT_ESK = DECODER.decode("FXo6o+L2ajCwKOWIhs5ozm70JlJdVpO2j9Z/7QJpJfduIy8VwG27tqPuqiRH9PrP9bsjg4pqfuySz4Sp5RVTe4r7a6m/3m5T46Yg7KH/qzIxkhNyKTzv+tW0NNnOjv4MW64WiZ8o6X8U6yxpH9mnI8kneQ8BhkFGjUVj+oNqNavMEDyt7VRG9Xa5ownxFs631bbYyz6TGaeh0zRcSabo3G2XECIQlckJgMGTj/TtsiR67XZr1lWms+9duDCRbA+VXpandPiFGaNz8qSLOj2ues2+Ho7b9WB4SP0Uba/SzcEMA9KKjCVD+CDAfa9ROM3jCHmIv2eNZE89Ka1dcdJtOQ==");
	public static final BigInteger INITIAL_CHECKPOINT_LS = new BigInteger("12");
	public static final BigInteger INITIAL_CHECKPOINT_TS = new BigInteger("1634898376493");
	public static final byte[] INITIAL_CHECKPOINT_HMAC = DECODER.decode("vd2rFQG3i3oNDnfKM6uD1U9ze4OUgu57tWho+B+5loI=");
	public static final byte[] INITIAL_CHECKPOINT_SIGNATURE = DECODER.decode("clB7rtgoi6e5M9XkYf4j/1j9Jx2ZkaSSLITJ+cpUucitUTLDS9TZSrIxiRoPjkqVwDE+8CIb4rQ1iqNCHKAyr2ieRaQvksnTdgkA1SGP4Z232tn6GT3UWHga8likqPDu1jP/cyH8vFrAXJDx8NsN34243XoQ2sOZWBaBEaulKTKFMlyPkAz2+BzXF/ClWb1TTmRraIzqbe2CSILb6uQ2hIKnJCalXvTK1jH6B/dgj7ra/xe9D9dORUA2Gbe27UbhOahHnLUNO/tOGJ5INfgrbKTazTGkYWO1hiVjYJw9WNvDBZh4wzvTP6IZZW4BExjp3beUUgQ874NgtJKMxsvvIQ==");
	public static final BigInteger INITIAL_CHECKPOINT_MAX_LINES = new BigInteger("12");
	public static final byte[] INITIAL_CHECKPOINT_LSK = DECODER.decode("");
	public static final byte[] INITIAL_CHECKPOINT_PHMAC = DECODER.decode("");
	private static final CheckpointMetadata INITIAL_CHECKPOINT_METADATA_OBJECT = new CheckpointMetadata(
			TestData.INITIAL_CHECKPOINT_LSK, TestData.INITIAL_CHECKPOINT_ESK, TestData.INITIAL_CHECKPOINT_PHMAC,
			TestData.INITIAL_CHECKPOINT_MAX_LINES, TestData.INITIAL_CHECKPOINT_TS, TestData.INITIAL_CHECKPOINT_HMAC,
			TestData.INITIAL_CHECKPOINT_SIGNATURE
	);
	public static final CheckpointLogLine INITIAL_CHECKPOINT_OBJECT =
			new CheckpointLogLine(TestData.INITIAL_CHECKPOINT_MESSAGE, INITIAL_CHECKPOINT_METADATA_OBJECT);

	public static final String CHECKPOINT_LOG_LINE = "2021-10-22 12:26:16.948+0200 INFO  SecureLogAppender: - [CHECKPOINT: Line counter]{\"LSK\":\"8mR+wTqcLglIzeeRBhwIQlCESQTHcR1O08KahXCLzFY=\",\"ESK\":\"RFkroRCjrA+DS0ZfnQAmsJpqyZj6XLSGlEU6qLfYzBK/ziXdrnv75a9ejsYNgWf+YGCUOnZDQf2RCk9Sui83e55+aPkRrQdzaDknqJudgPbAl/EnBqEP300zyaMr9fyl/O+VTOwPXdI5MSWeSqjyg6QjC5oM0YTJ83ou5b0zIq0yAWCvOt4Si0WzLtBVLIPJK0stkybsz45yntx/xcVa+T+Le/bj0i26UXD9iEI3tavIaBx4h2h1zQhjbExIjNsaRSA/2FFEQC6WwzXCIV0S/wWLoYQGBjQp5FOqA2uTxwTwNNMqC5JPbuVElyNV0Snx4sVHImKOs0ijKItNKNyisQ==\",\"PHMAC\":\"UCDqw2Ltgn6Tozv53frcc6bL6nGdhJE68Vxofns1pFk=\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"a+V2FYws3I4sF0FqN1CNns7OXEzZN+DkPu/K2QCyW8k=\",\"SG\":\"fYJarfU9b9KSKpzhPVZ6DSD+fl8BY4MIBpz82LqqAimpcH6ubaafCCHl0MFlcsKaYl490YizFCv6SeWYBpuRAdCj+FUN0HFW9rLAa4OAIuzB4HJYPqocZHHGKkvzjLi5GM3Oz7R/aoY2JR93npGr+rML3ghA0JK87LYjUsB0WblAuIpQxm83xXtnTnE+zYKJwwPCHeN6BbyK+FtZLMR/L6zJ4qfc8bt6JOqmm6j/gOa3ntJE1FQqBbhgNqIEBU6LLfX2/Vvo49jeFIyOT1dE3tStuE4mr4Jo0xoEznfYOrAys67v/CpvrVsdRdUJrr/GyoHkQs0gMfe1HchQefLF0A==\"}\n";
	public static final String CHECKPOINT_METADATA = "{\"LSK\":\"8mR+wTqcLglIzeeRBhwIQlCESQTHcR1O08KahXCLzFY=\",\"ESK\":\"RFkroRCjrA+DS0ZfnQAmsJpqyZj6XLSGlEU6qLfYzBK/ziXdrnv75a9ejsYNgWf+YGCUOnZDQf2RCk9Sui83e55+aPkRrQdzaDknqJudgPbAl/EnBqEP300zyaMr9fyl/O+VTOwPXdI5MSWeSqjyg6QjC5oM0YTJ83ou5b0zIq0yAWCvOt4Si0WzLtBVLIPJK0stkybsz45yntx/xcVa+T+Le/bj0i26UXD9iEI3tavIaBx4h2h1zQhjbExIjNsaRSA/2FFEQC6WwzXCIV0S/wWLoYQGBjQp5FOqA2uTxwTwNNMqC5JPbuVElyNV0Snx4sVHImKOs0ijKItNKNyisQ==\",\"PHMAC\":\"UCDqw2Ltgn6Tozv53frcc6bL6nGdhJE68Vxofns1pFk=\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"a+V2FYws3I4sF0FqN1CNns7OXEzZN+DkPu/K2QCyW8k=\",\"SG\":\"fYJarfU9b9KSKpzhPVZ6DSD+fl8BY4MIBpz82LqqAimpcH6ubaafCCHl0MFlcsKaYl490YizFCv6SeWYBpuRAdCj+FUN0HFW9rLAa4OAIuzB4HJYPqocZHHGKkvzjLi5GM3Oz7R/aoY2JR93npGr+rML3ghA0JK87LYjUsB0WblAuIpQxm83xXtnTnE+zYKJwwPCHeN6BbyK+FtZLMR/L6zJ4qfc8bt6JOqmm6j/gOa3ntJE1FQqBbhgNqIEBU6LLfX2/Vvo49jeFIyOT1dE3tStuE4mr4Jo0xoEznfYOrAys67v/CpvrVsdRdUJrr/GyoHkQs0gMfe1HchQefLF0A==\"}\n";
	public static final String CHECKPOINT_MESSAGE = "2021-10-22 12:26:16.948+0200 INFO  SecureLogAppender: - [CHECKPOINT: Line counter]";
	public static final byte[] CHECKPOINT_LSK = DECODER.decode("8mR+wTqcLglIzeeRBhwIQlCESQTHcR1O08KahXCLzFY=");
	public static final byte[] CHECKPOINT_ESK = DECODER.decode("RFkroRCjrA+DS0ZfnQAmsJpqyZj6XLSGlEU6qLfYzBK/ziXdrnv75a9ejsYNgWf+YGCUOnZDQf2RCk9Sui83e55+aPkRrQdzaDknqJudgPbAl/EnBqEP300zyaMr9fyl/O+VTOwPXdI5MSWeSqjyg6QjC5oM0YTJ83ou5b0zIq0yAWCvOt4Si0WzLtBVLIPJK0stkybsz45yntx/xcVa+T+Le/bj0i26UXD9iEI3tavIaBx4h2h1zQhjbExIjNsaRSA/2FFEQC6WwzXCIV0S/wWLoYQGBjQp5FOqA2uTxwTwNNMqC5JPbuVElyNV0Snx4sVHImKOs0ijKItNKNyisQ==");
	public static final BigInteger CHECKPOINT_TS = new BigInteger("1634898376948");
	public static final byte[] CHECKPOINT_HMAC = DECODER.decode("a+V2FYws3I4sF0FqN1CNns7OXEzZN+DkPu/K2QCyW8k=");
	public static final byte[] CHECKPOINT_PHMAC = DECODER.decode("UCDqw2Ltgn6Tozv53frcc6bL6nGdhJE68Vxofns1pFk=");
	public static final byte[] CHECKPOINT_SIGNATURE = DECODER.decode("fYJarfU9b9KSKpzhPVZ6DSD+fl8BY4MIBpz82LqqAimpcH6ubaafCCHl0MFlcsKaYl490YizFCv6SeWYBpuRAdCj+FUN0HFW9rLAa4OAIuzB4HJYPqocZHHGKkvzjLi5GM3Oz7R/aoY2JR93npGr+rML3ghA0JK87LYjUsB0WblAuIpQxm83xXtnTnE+zYKJwwPCHeN6BbyK+FtZLMR/L6zJ4qfc8bt6JOqmm6j/gOa3ntJE1FQqBbhgNqIEBU6LLfX2/Vvo49jeFIyOT1dE3tStuE4mr4Jo0xoEznfYOrAys67v/CpvrVsdRdUJrr/GyoHkQs0gMfe1HchQefLF0A==");
	public static final byte[] CHECKPOINT_HMAC_KEY = DECODER.decode("DysIvhMG6XSwm8eVPqz+Bf+hJijmqJIA/KSn1huHWSE=");
	public static final BigInteger CHECKPOINT_MAX_LINES = new BigInteger("12");
	public static final CheckpointMetadata CHECKPOINT_METADATA_OBJECT = new CheckpointMetadata(
			TestData.CHECKPOINT_LSK, TestData.CHECKPOINT_ESK, TestData.CHECKPOINT_PHMAC, TestData.CHECKPOINT_MAX_LINES, TestData.CHECKPOINT_TS,
			TestData.CHECKPOINT_HMAC,
			TestData.CHECKPOINT_SIGNATURE
	);
	public static final CheckpointLogLine CHECKPOINT_LOG_LINE_OBJECT =
			new CheckpointLogLine(TestData.CHECKPOINT_MESSAGE, CHECKPOINT_METADATA_OBJECT);

	public static final String REGULAR_LOG_LINE = "2021-10-22 12:26:16.926+0200 INFO  SecureLog:89 - {\"incrementOrder\":0}{\"TS\":\"1634898376926\",\"HMAC\":\"lvTo9PHiMtZKux+9P0frEvYYbTtaDYkJpowK3BDZIgI=\",\"LC\":\"0\"}\n";
	public static final String REGULAR_MESSAGE = "2021-10-22 12:26:16.926+0200 INFO  SecureLog:89 - {\"incrementOrder\":0}";
	public static final String REGULAR_METADATA = "{\"TS\":\"1634898376926\",\"HMAC\":\"lvTo9PHiMtZKux+9P0frEvYYbTtaDYkJpowK3BDZIgI=\",\"LC\":\"0\"}\n";
	public static final BigInteger REGULAR_TS = new BigInteger("1634898376926");
	public static final byte[] REGULAR_HMAC = DECODER.decode("lvTo9PHiMtZKux+9P0frEvYYbTtaDYkJpowK3BDZIgI=");
	public static final BigInteger REGULAR_LC = new BigInteger("0");
	public static final byte[] REGULAR_HMAC_KEY = DECODER.decode("8mR+wTqcLglIzeeRBhwIQlCESQTHcR1O08KahXCLzFY=");
	public static final byte[] REGULAR_PHMAC = DECODER.decode("vd2rFQG3i3oNDnfKM6uD1U9ze4OUgu57tWho+B+5loI=");
	private static final RegularLogLineMetadata REGULAR_METADATA_OBJECT = new RegularLogLineMetadata(
			TestData.REGULAR_HMAC,
			TestData.REGULAR_TS,
			TestData.REGULAR_LC);
	public static final RegularLogLine REGULAR_OBJECT =
			new RegularLogLine(TestData.REGULAR_MESSAGE, REGULAR_METADATA_OBJECT);

	static final BigInteger MODULUS = new BigInteger("26301719652334546954916971024957090557155633952836171778395524424589532113881382669320683688633359563202619152539020408789708973700888022523303722972666854819165315808023337372045885174499785539416329388168669917639695489292413737675049663133918232300762578249298149469461243909209924016614670064908031848761587533930870044516538876086490906359757065091133627777295195584454302866049417625581932382672303934188024287822811046829554506755767055479066362086345501823425227641878676410966849382529642683690313171990363921140739544671120436336577095567220193315034259983257484542666303405627198299040827515548816635986327");
	static final BigInteger PUBLIC_EXPONENT = new BigInteger("65537");
}
