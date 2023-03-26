import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.LinkedHashMap;

import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.springframework.util.Assert;

public class Log5W {

	private static class JsonHelper {
		
		private static ObjectMapper mapper = new ObjectMapper();
		
		public static String json(Object object) {
			try {
				return mapper.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public static class ProntoParaLogar {
		
		private Map<String, String> parametros = new LinkedHashMap<>();
		
		public ProntoParaLogar(String metodo, String oQueEstaAcontecendo,
				Map<String, String> infosAdicionais) {
			
			parametros.put("metodo", metodo);
			parametros.put("oQueEstaAcontecendo", oQueEstaAcontecendo);
			
			infosAdicionais.entrySet().forEach(entrada -> {
				parametros.put(entrada.getKey(), entrada.getValue());
			});
		}
		
		public ProntoParaLogar adicionaInformacao(String chave, String informacao) {
			
			Assert.hasText(chave, "A chave não pode ser vazia");
			Assert.hasText(informacao, "A informação não pode ser vazia");
			Assert.isNull(parametros.get(chave), "A chave já foi adicionada");
			
			parametros.put(chave, informacao);
			return this;
		}
		
		public String debug(@NotNull Logger logger) {
			String json = JsonHelper.json(this.parametros);
			logger.debug(json);
			
			return json;
		}
		
		public String info(@NotNull Logger logger) {
			String json = JsonHelper.json(this.parametros);
			logger.error(json);
			
			return json;
		}
		
		public String erro(@NotNull Logger logger) {
			String json = JsonHelper.json(this.parametros);
			logger.error(json);
			
			return json;
		}
		
		public String erro(@NotNull Logger logger, @NotNull Exception exception) {
			String json = JsonHelper.json(this.parametros);
			logger.error(json, exception);
			
			return json;
		}
		
		public String mensagem() {
			return JsonHelper.json(this.parametros);
		}
	}
	
	
	public static class OqueEstaAcontecendo {
		
		private String metodo;
		private String oQueEstaAcontecendo;
		private Map<String, String> infosAdicionais = new LinkedHashMap<>();
		
		public OqueEstaAcontecendo(String metodo, String oQueEstaAcontecendo) {
			this.metodo = metodo;
			this.oQueEstaAcontecendo = oQueEstaAcontecendo;
		}
		
		public ProntoParaLogar adicionaInformacao(String chave, String informacao) {
			
			Assert.hasText(chave, "A chave não pode ser vazia");
			Assert.hasText(informacao, "A informação não pode ser vazia");
			Assert.isNull(infosAdicionais.get(chave), "A chave já foi adicionada");
			
			infosAdicionais.put(chave, informacao);
			return new ProntoParaLogar(metodo, oQueEstaAcontecendo, infosAdicionais);
		} 
	}
	
	public static class Metodo {
		
		private String metodo;
		
		public Metodo(String metodo) {
			Assert.hasText(metodo, "O metodo não pode ser vazio");
			this.metodo = metodo;
		}
		
		public OqueEstaAcontecendo oQueEstaAcontecendo(String oQueEstaAcontecendo) {
			Assert.hasText(oQueEstaAcontecendo, "O momento não pode ser vazio");
			return new OqueEstaAcontecendo(metodo, oQueEstaAcontecendo);
		}
	}
	
	public static Metodo metodo(String metodo) {
		return new Metodo(metodo);
	}
	
	public static Metodo metodo() {
		 StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		 
		 // Deve ser a posição 2 pois a posição 0 é a chamada do stacktrace
		 // A posição 1 é a invocação do proprio metodo
		 // E a posição 2 é o ponto de origem
		 return new Metodo(stackTrace[2].getMethodName());
	}
	
}


