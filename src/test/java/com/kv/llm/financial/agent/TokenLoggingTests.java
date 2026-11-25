package com.kv.llm.financial.agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(properties = "huggingface.access-token=" + TokenLoggingTests.DUMMY_TOKEN)
class TokenLoggingTests {

	static final String DUMMY_TOKEN = "hf_dummy_value_for_test";

	@Test
	void accessTokenIsNotLogged(CapturedOutput output) {
		// Guards against a capture that sees no log output at all.
		assertThat(output.getAll()).contains("Initialized DJL HuggingFaceTokenizer");
		assertThat(output.getAll()).doesNotContain(DUMMY_TOKEN);
	}

}
