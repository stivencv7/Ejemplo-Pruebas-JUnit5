package org.velasco.junit5app.ejemplo.models;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.velasco.junit5app.ejemplo.exceptions.DineroInsuficienteException;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaTest {
	 Cuenta cuenta;
	 private TestInfo testInfo;
	 private TestReporter testReporte;
	 
	 @BeforeEach
	 void initMetodoTest(TestInfo testInfo,TestReporter testReporte) {
		this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		this.testInfo=testInfo;
		this.testReporte=testReporte;
		System.out.println("Iniciando el metodo.");
		testReporte.publishEntry("ejecutando "+testInfo.getDisplayName()+" "+testInfo.getTestMethod().orElse(null).getName()
				+ " con las etiquetas" + testInfo.getTags());
	}
	@AfterEach
	void tearDown() {
		System.out.println("finalizando el metodo de prueba");
	}
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("inicializando el test");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("finalizando el test");
	}
	
	@Tag("cuenta")
	@Nested
	@DisplayName("probando atrributos de la cuenta ")
	class CuentaTestNombreSaldo{
		@Test
		@DisplayName("el nombre")
		void testNombreCuenta() {
			testReporte.publishEntry(testInfo.getTags().toString());
			if(testInfo.getTags().contains("cuenta")) {
				testReporte.publishEntry("Hacer algo con la etiqueta cuenta");
			}
			String esperado = "Andres";
			String real = cuenta.getPersona();
			assertNotNull(real,()->"La cuenta no puede ser nula");
			assertEquals(esperado, real,()->"El nombre de la cuenta no es el esperado: se esperava" + esperado);
			assertTrue(real.equals("Andres"),()-> "nombre cuenta esperada debe ser igual a la real");
		}
		
		@Test
		@DisplayName("el saldo que no sea null,mayor que 0,valor esperado!")
		void testSaldoCuenta() {
			
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
		
		@Test
		@DisplayName("probando referencia de la cuenta!")
		void testReferenciaCuenta() {
			cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
			Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
			// assertNotEquals(cuenta2, cuenta);
			assertEquals(cuenta2, cuenta);
		}
		
	}
	@Nested
	class CuentaOperacionesTest{
		@Tag("cuenta")
		@Test
		//@DisplayName("probando el debito!")
		void testDebitoCuenta() {
			
			cuenta.debito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(900, cuenta.getSaldo().intValue());
			assertEquals("900.12345", cuenta.getSaldo().toPlainString());
		}
		@Tag("cuenta")
		@Test
		@DisplayName("probando el credito de la cuenta!")
		void testCreditoCuenta() {
			
			cuenta.credito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(1100, cuenta.getSaldo().intValue());
			assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
		}
		
		@Tag("cuenta")
		@Tag("banco")
		@Test
		@DisplayName("probando que la operacion de la transferencia!")
		void testTransferirDineroCuenta() {
			Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
			Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

			Banco banco = new Banco();
			banco.setNombre("Banco del Estado");
			banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
			assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
			assertEquals("3000", cuenta1.getSaldo().toPlainString());
		}
	}
	
	

	@Test
	@Tag("cuenta")
	@Tag("error")
	//@DisplayName("probando que el dinero no sea insuficiente!")
	void testDineroInsuficiente() {
		Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
			cuenta.debito(new BigDecimal(1500));
		});
		String actual = exception.getMessage();
		String esperado = "Dinero Insuficiente";
		assertEquals(esperado, actual);
	}

	@Test
	@Tag("cuenta")
	@Tag("banco")
	@DisplayName("probando la relacion de cuenta con el banco utilizando assertAll!")
	void testRelacionBancoCuentas() {
//		fail();
		
		Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

		Banco banco = new Banco();
		banco.addCuenta(cuenta1);
		banco.addCuenta(cuenta2);

		banco.setNombre("Banco del Estado");
		banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
		assertAll(() -> 
			assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
					()->"El valor del saldo de la cuenta2  no es el esperado ")
		, () -> 
			assertEquals("3000", cuenta1.getSaldo().toPlainString(),
					()->"El valor del saldo de la cuenta1  no es el esperado ")
		, () -> 
			assertEquals(2, banco.getCuentas().size(),
					()->"El banco no tiene las cuentas esperadas ")
		, () -> 
			assertEquals("Banco del Estado", cuenta1.getBanco().getNombre())
		, () -> 
			assertEquals("Andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("Andres")).findFirst()
					.get().getPersona())
		, () -> 
			assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("John Doe")))
		);//En este metodo assertAll probamos mas de una prueba 

	}
	@Nested
	class SistemaOperativo{
		@Test
		@EnabledOnOs(OS.WINDOWS)
		void testSoloWindows(){}
		
		@Test
		@EnabledOnOs({OS.LINUX,OS.MAC})
		void testSoloLinuxMac() {}
		
		@Test
		@DisabledOnOs(OS.WINDOWS)//para q no siva para windows
		void testNoWindows() {}
	}
	@Nested
	class JavaVersionTest{
		
		@Test()
		@EnabledOnJre(JRE.JAVA_8)
		void testSoloJdk8() {}
		
		@Test
		@EnabledOnJre(JRE.OTHER)
		void testSoloJDK15() {}
		
		@Test
		@DisabledOnJre(JRE.OTHER)
		void testNoSoloJDK15() {}
		
	}
	
	@Nested
	class systemPropertiesTest{
		
		@Test
		//este metodo nos permite ver datos del java y el sistema operativo etc
		void ImpirmirSystemProperties() {
			Properties properties=System.getProperties();
			properties.forEach((k,v)->System.out.println(k + ":" + v));
		}
		@Test
		@EnabledIfSystemProperty(named="java.version",matches=".*17.*")//para avilitar propiedades del sistema (.*17.*" estamos aproximandonos a la version de java que tenemos )
		void testJavaVersion(){}
		
		@Test
		@DisabledIfSystemProperty(named = "os,arch",matches =".*32.*" )//para desabilitar propiedades del sistema (.*17.*" estamos aproximandonos a la version de java que tenemos )
		void testSolo64(){}
		
		@Test
		@EnabledIfSystemProperty(named = "os,arch",matches =".*32.*" )
		void testNo64(){}
		
		@Test
		@EnabledIfSystemProperty(named = "user.name",matches ="ermen" )
		void testNombreUser(){}

		
		@Test
		@EnabledIfSystemProperty(named = "ENV",matches ="dev" )
		void testDev(){}
	}
	
	@Nested
	class variableAnviente{
		@Test
		void imprimirVariablesAmbiente() {
			Map<String,String> getenv=System.getenv();
			getenv.forEach((k,v)->System.out.println(k + " = "+v));
		}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "JAVA_HOME",matches = ".*jdk-17.0.2.8-hotspot.*")
		void testJavaHome() {}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS",matches = "12")
		void testProcesadores() {}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "ENVIRONMENT",matches ="dev" )
		void testEnv(){}
		
		@Test
		@DisabledIfEnvironmentVariable(named="ENVIRONMENT",matches = "prod")
		void testEnvProdDisabled(){}
		
		@Test
		@DisplayName("probando saldoDev de la cuenta!")
		void testSaldoCuentaDev() {
			
			boolean esDev="dev".equals(System.getProperty("ENV"));
			assumeTrue(esDev);
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
		
		@Test
		@DisplayName("probando saldoDev2 de la cuenta!")
		void testSaldoCuentaDev2() {
			
			boolean esDev="dev".equals(System.getProperty("ENV"));
			assumingThat(esDev,()->{
				assertNotNull(cuenta.getSaldo());
				assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
				
			});
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);	
		}
	}
	@Tag("param")
	@Nested
	class PruebasParametrizadas {
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@ValueSource(strings = {"100","200","300","500","700","1000"})
		//@DisplayName("probando el debito!")
		void testDebitoCuentaValuesourse(String monto) {
			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
		}
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000"})
			void testDebitoCuentaCsvSourse(String index,String monto) {
			System.out.println(index + " -> "+monto);
			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
		}
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvSource({"200,100,Andres,Andres","250,200,Camilo,Camilo","350,300,Estiben,Estiben","510,500,Sebastian,Sebastian","750,700,Boss,Boss","1000.123457,1000.12345,Orlando,Orlando"})
			void testDebitoCuentaCsvSourse2(String saldo,String monto,String esperado,String actual) {
			System.out.println(saldo + " -> "+monto);
			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(monto));
			cuenta.setPersona(actual);
			
			assertNotNull(cuenta.getSaldo());
			assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
			;
		}
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvFileSource(resources = "/data.csv")
			void testDebitoCuentaCsvFileSource(String monto) {
			
			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
		}
		
		
		
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvFileSource(resources = "/data2.csv")
			void testDebitoCuentaCsvFileSource2(String saldo,String monto,String esperado,String actual) {
			
			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(monto));
			cuenta.setPersona(actual);
			
			
			assertNotNull(cuenta.getSaldo());
			assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
		}
		@RepeatedTest(value=5,name = "{displayName}-Repeticion numero {currentRepetition} de {totalRepetitions}")
		void testDebitoCuentaRepetir(RepetitionInfo info) {
			if(info.getCurrentRepetition()==3) {
				System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
			}
			cuenta.debito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(900, cuenta.getSaldo().intValue());
			assertEquals("900.12345", cuenta.getSaldo().toPlainString());
		}
	}
	
	@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
	@MethodSource("montoList")
		void testDebitoCuentaMethodSource(String monto) {
		
		cuenta.debito(new BigDecimal(monto));
		assertNotNull(cuenta.getSaldo());
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
	}
	
	static List<String> montoList(){
		return Arrays.asList("100","200","300","500","700","1000");
	}
	
	@Nested
	@Tag("timeout")
	class EjemploTimeoutTest{
		@Test
		@Timeout(1)
		void pruebaTimeout() throws InterruptedException {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		
		@Test
		@Timeout(value = 1000,unit = TimeUnit.MILLISECONDS)
		void pruebaTimeout2() throws InterruptedException {
			TimeUnit.MILLISECONDS.sleep(900);
		}
		
		@Test
		void testTimeoutAssertions()  {
			assertTimeout(Duration.ofSeconds(5),()->{
				TimeUnit.MILLISECONDS.sleep(4000);
			});
		}
	}
}
