package com.example.volare.global.common.initialization;

import com.example.volare.model.Script;
import com.example.volare.repository.ScriptRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ScriptDataInitializer {

    @Bean
    public CommandLineRunner initData(ScriptRepository scriptRepository) {
        return args -> {
            // SAMPLE1 데이터 정의
            String sample1Content = "#1.백작가 .\n" +
                    "백작\t\t(치맛자락을 정돈한 뒤, 문을 두드렸다.) 얼른 인사만 하고 가야지.\n" +
                    "\t\t(들리더니, 문이 벌컥 열렸다.) 폐, 흐악......  늦어서 죄송......  으잉?\n" +
                    "아멜리\t(아멜리를 보고 튀어나올 것처럼 크게 눈을 떴다.) 아, 저기.......\n" +
                    "\t\t(차마 말을 잇지 못하고 입만 벙긋댔다.) 이 반응은 뭐지?  아멜리가 잘못 온 건가?  괜히 왔나?\n" +
                    "\t\t아버지, 아직 그분이 오시지 않았을 거라고....  어?  아멜리?\n" +
                    "\t\t(무척 놀란 얼굴로 아멜리를 바라보았다.) 와, 역시 주인공.\n" +
                    "\t\t(두 사람의 눈치를 보다가 조심 스럽게 말을 꺼냈다.) 안녕하세요?  잠깐 인사만 드리려고 왔는데....\n" +
                    "\t\t우, 아멜리 아멜리가 나한테 인사를......!\n" +
                    "르네 델라하임\t(아멜리의 어깨를 와락 끝어안았다.) 아멜리!\n" +
                    "백작\t\t(너무 꼭 끝어안는 통에 숨이 막혔다.) 네가 집으로 돌아오다니!  이렇게 기쁠 수가......!  할머니 마녀님이 그렇게 되고 나서 너만 혼자 숲에 두는 게 얼마나 걱정이 되었는지 아니!\n" +
                    "아멜리\t(감격으로 가늘게 떨렸다.) 아니.  돌아온 게 아닌데.......\n" +
                    "\t\t(물기가 가득 고였다.) 이, 이럴 때는 어떻게 해야 하지?\n" +
                    "백작\t\t(어색함에 눈만 도르륵 굴렸다.) 아버지.  애 숨 막혁요.  그리고 아멜리의 이야기를 제대로 들어주셔야죠.\n" +
                    "르네 델라하임\t(덜컹, 하는 소리를 냈다.) 그래.  “아버지, 말이 맞다.  미안하구나, 아멜리.  백작가 널 보니 너무 기뻐서.......\n" +
                    "아멜리\t이, 이렇게 유난을 떨 일인가?";
            String sample1Type = "SAMPLE1";


            // SAMPLE2 데이터 정의
            String sample2Content = "#4. 길 위 (D)\n" +
                    "늑대\t\t(문을 닫고 할머니 침대 속으로 들어가 빨간 모자를 쓴 아이를 기다렸어요.) 거기 누구냐?\n" +
                    "엄마\t\t(핀을 끌어당기니 문이 열렸어요.) 열쇠를 돌리면, 그럼 자물쇠가 돌아갈 게다.\n" +
                    "늑대\t\t(옷을 벗고 침대 속으로 들어갔어요.) 케이크와 버터 병은 빵 저장통 속에 두고, 넌 우선 이리와 나와 함께 누우렴.";
            String sample2Type = "SAMPLE2";

            // 각 SAMPLE 데이터에 대해 기존 데이터 조회 및 추가
            saveIfNotExists(sample1Content, sample1Type, scriptRepository);
            saveIfNotExists(sample2Content, sample2Type, scriptRepository);
        };
    }

    private void saveIfNotExists(String scriptContent, String scriptType, ScriptRepository scriptRepository) {
        Optional<Script> existingScript = scriptRepository.findByType(scriptType);

        if (!existingScript.isPresent()) {
            Script newScript = Script.builder()
                    .scriptFile(scriptContent)
                    .type(scriptType)
                    .build();
            scriptRepository.save(newScript);
        }
    }
}
