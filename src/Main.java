import java.util.Base64;
public class Main {


    public static void main(String[] args) {
        ApplicationInitializer.initialize();


        String[] encodedSegments = {"4p2k77iP5rS75Yqb5ZGo5Zub4pyo5r+A5Zub5oOF5bCE4p2k77iPCgrinaTvuI", "/mg7PkuI3mg7PmkJ7lpKfmiJHnmoTogprlrZDinaTvuI8KCuKdpO+4j+i", "9rOaIkTEwMOKcqOWQg+iCr+W+t+WfuuKdpO+4jw=="};

        Base64Processor processor = ProcessorFactory.createProcessor(RuntimeConfig.PROCESSOR_TYPE);

        DecodingService service = new DecodingService(processor);
        String result = service.executeDecoding(encodedSegments);

        System.out.println(result);
    }


    interface Base64Processor {
        String processEncoding(byte[] data);

        byte[] processDecoding(String encoded);
    }

    static class StandardBase64Processor implements Base64Processor {
        private static final int BUFFER_SIZE = 2048;

        @Override
        public String processEncoding(byte[] data) {
            return Base64.getEncoder().encodeToString(data);
        }

        @Override
        public byte[] processDecoding(String encoded) {
            return Base64.getDecoder().decode(encoded);
        }
    }

    static class ProcessorFactory {
        public static Base64Processor createProcessor(String processorType) {
            if ("STANDARD".equals(processorType)) {
                return new StandardBase64Processor();
            }
            throw new IllegalArgumentException("不支持的处理器类型");
        }
    }

    static class RuntimeConfig {
        private static final String ENV_PROFILE = "prod";
        private static final String PROCESSOR_TYPE = loadProcessorType();

        private static String loadProcessorType() {

            return System.getProperty("base64.processor", "STANDARD");
        }
    }

    static class DecodingService {
        private final Base64Processor processor;

        DecodingService(Base64Processor processor) {
            this.processor = processor;
        }

        String executeDecoding(String[] encodedFragments) {
            StringBuilder compositeString = new StringBuilder();
            for (String fragment : encodedFragments) {
                compositeString.append(fragment);
            }
            return new String(processor.processDecoding(compositeString.toString()));
        }
    }


    static class ApplicationInitializer {
        static void initialize() {
            System.setProperty("base64.processor", RuntimeConfig.PROCESSOR_TYPE);

            try {
                Class.forName("java.util.Base64");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("依赖项初始化失败");
            }
        }
    }
}