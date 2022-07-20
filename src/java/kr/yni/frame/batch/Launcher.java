package kr.yni.frame.batch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kr.yni.frame.util.FileUtil;

/**
 * 배치(Java) 프로그램을 독립된 프로세스로 실행시키는 기능을 제공한다.
 * 
 * @author YNI-maker
 * 
 */
public class Launcher {
	private Log log = LogFactory.getLog(Launcher.class);

	/**
	 * 실행할 child java의 환경 변수를 담아놓는다.
	 */
	private Map<String, String> envMap = new HashMap<String, String>();

	/**
	 * 실행할 child java의 시스템 프로퍼티를 담아 놓는다.
	 */
	private Map<String, String> sysProps = new HashMap<String, String>();

	/**
	 * 실행할 child java의 JVM 설정값들을 담아 놓는다.
	 */
	private List<String> jvmArgs = new ArrayList<String>();

	/**
	 * 실행할 child java의 classpath들을 담아 놓는다.
	 */
	private List<String> classPath = new ArrayList<String>();

	/**
	 * 실행할 child java 프로그램의 응용 프로그램 파라메터를 담아 놓는다.
	 */
	private List<String> arguments = new ArrayList<String>();

	/**
	 * 실행할 child java의 working directory를 담아 놓는다.
	 */
	private File workDir = null;

	/**
	 * 실행할 child java의 실행 클래스를 담아 놓는다.
	 */
	private String mainClass = null;

	public Launcher(String className) {
		mainClass = className;
	}

	public void start(List<HashMap<String, Object>> argVO) throws Exception {
		start(argVO, false);
	}

	@SuppressWarnings("rawtypes")
	public void start(List<HashMap<String, Object>> argVO, boolean waitFor)
			throws Exception {
		List<String> argList = new ArrayList<String>();
		String[] args = null;
		
		if (argVO != null) {
			for (int i = 0; i < argVO.size(); i++) {
				HashMap<String, Object> map = argVO.get(i);
				Iterator itor = map.keySet().iterator();
				
				while (itor.hasNext()) {
					String key = (String) itor.next();
					Object value = map.get(key);
					
					if (value != null) {
						String strValue = (String) map.get(key).toString();
						argList.add(key.trim() + "," + i + ":" + strValue.trim());
					}
				}
			}
			args = argList.toArray(new String[argList.size()]);
		}

		start((String[]) args, waitFor);
	}

	public void start(String argStr) throws Exception {
		start(argStr, false);
	}

	public void start(String argStr, boolean waitFor) throws Exception {
		String[] args = null;
		if (argStr != null) {
			args = argStr.split(" ");
		}

		start((String[]) args, waitFor);
	}

	public void start(String[] args) throws Exception {
		start(args, false);
	}

	/**
	 * 설정된 내역과 주어진 응용프로그램 파라메터를 사용하여 독립된 프로세스로 Java 프로그램을 실행한다.
	 * 
	 * @param args
	 *            응용프로그램 파라메터 목록
	 * @param waitFor
	 *            실행된 프로그램이 종료될 때까지 대기할지 여부(true/false)
	 * @throws Exception
	 * @throws IOException
	 */
	public void start(String[] args, boolean waitFor) throws Exception {
		List<String> command = new ArrayList<String>();

		command.add(getJavaCommand()); // Java 명령 추가(%JAVA_HOME%\bin\javaw.exe)

		// JVM 설정을 다음에 넣는다.
		command.addAll(getJvmArgs());

		// 시스템 프로퍼티 3번째로 넣는다.
		command.addAll(getSystemProperties());

		// Classpath를 넣는다.
		String cp = getClasspath();
		if (cp.length() > 0) {
			command.add("-classpath");
			command.add(cp);
		}

		// 실행할 메인 클래스 명을 넣는다.
		command.add(mainClass);

		// 설정되어 있는 응용프로그램 인자를 넣는다.
		command.addAll(getArguments());

		// 마지막으로 파라메터로 전달된 응용프로그램 인자들을 넣는다.
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				command.add(args[i]);
			}
		}

		// System.out.println(command);
		if (log.isDebugEnabled()) {
			log.debug("Batch launched : " + command);
		}

		String[] cmdarray = command.toArray(new String[command.size()]);
		Runtime rt = Runtime.getRuntime();

		try {
			Process proc = rt.exec(cmdarray, getEnvironment(), workDir);

			if (waitFor) {
				StreamConnector stdout = new StreamConnector(
						proc.getInputStream(), System.out);
				StreamConnector stderr = new StreamConnector(
						proc.getErrorStream(), System.err);
				stdout.start();
				stderr.start();
				try {
					proc.waitFor();
					stdout.join();
					stderr.join();
				} catch (InterruptedException e) {
				}
			} else {
				proc.getInputStream().close();
				proc.getErrorStream().close();
			}
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
	 * path 하위에 있는 모든 JAR 파일들을 클래스 패스로 추가한다.
	 * 
	 * @param path
	 */
	public void addJars(String root) {
		String[] jarFiles = FileUtil.getFilenamesUnder(root, new String[] { "jar" }, true);
		for (int i = 0; i < jarFiles.length; i++) {
			classPath.add(root + File.separator + jarFiles[i]);
		}
	}

	public void addClasspath(String path) {
		classPath.add(path);
	}

	/**
	 * 등록된 클래스패스들을 하나의 문자열로 반환한다.
	 * 
	 * @return
	 */
	public String getClasspath() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < classPath.size(); i++) {
			sb.append(classPath.get(i));
			sb.append(File.pathSeparator);
		}

		return sb.toString();
	}

	/**
	 * 환경 변수를 추가한다.
	 * 
	 */
	public void addEnvironment(String name, String value) {
		envMap.put(name, value);
	}

	/**
	 * 설정되어 있는 환경 변수들을 name=value 형태의 문자열 목록으로 반환한다.
	 * 
	 * @return
	 */
	private String[] getEnvironment() {
		if (envMap.size() == 0) {
			return null;
		} else {
			List<String> envList = new ArrayList<String>();
			Iterator<String> itor = envMap.keySet().iterator();
			while (itor.hasNext()) {
				String key = itor.next();
				envList.add(key + "=" + envMap.get(key));
			}

			return envList.toArray(new String[envList.size()]);
		}
	}

	/**
	 * 실행될 Java 프로세스의 working directory를 설정한다.
	 * 
	 * @param dir
	 */
	public void setDirectory(File dir) {
		workDir = dir;
	}

	/**
	 * 실행될 Java 프로세스의 JVM 설정 값을 추가한다.
	 * 
	 * @param args
	 */
	public void addJvmArgs(String args) {
		if (!StringUtils.isEmpty(args)) {
			String[] arglist = args.split(" ");
			for (int i = 0; i < arglist.length; i++) {
				jvmArgs.add(arglist[i]);
			}
		}
	}

	/**
	 * 설정되어 있는 JVM 설정 값들을 반환한다.
	 * 
	 * @return
	 */
	public List<String> getJvmArgs() {
		return jvmArgs;
	}

	/**
	 * 실행될 Java 프로그램의 시스템 프로퍼티를 설정한다.
	 * 
	 * @param name
	 * @param value
	 */
	public void addSystemProperty(String name, String value) {
		sysProps.put(name, value);
	}

	/**
	 * 설정되어 있는 시스템 프로퍼티 내역들을 반환한다.
	 * 
	 * @return
	 */
	public List<String> getSystemProperties() {
		if (sysProps.size() == 0) {
			return null;
		} else {
			List<String> sysPropList = new ArrayList<String>();
			Iterator<String> itor = sysProps.keySet().iterator();
			while (itor.hasNext()) {
				String key = itor.next();
				sysPropList.add("-D" + key + "=" + sysProps.get(key));
			}

			return sysPropList;
		}
	}

	/**
	 * 응용 프로그램 인자를 추가한다.
	 * 
	 * @param arg
	 */
	public void addArgument(String arg) {
		arguments.add(arg);
	}

	/**
	 * 설정되어 있는 응용프로그램 인자를 반환한다.
	 * 
	 * @return
	 */
	public List<String> getArguments() {
		return arguments;
	}

	/**
	 * Java 프로그램 실행을 위한 Java 명령의 경로를 반환한다. OS를 확인하여 해당 OS에 맞는 Java 명령의 경로를 반환한다.
	 * 
	 * @return
	 */
	protected String getJavaCommand() {
		String osName = System.getProperty("os.name").toLowerCase();
		String javaCommand = System.getProperty("java.home") + File.separator
				+ "bin" + File.separator;

		if (osName.indexOf("windows") >= 0) {
			javaCommand = "\"" + javaCommand + "javaw.exe\"";
		} else {
			javaCommand = javaCommand + "java";
		}

		return javaCommand;
	}

	/**
	 * Class for connectiong an OutputStream to an InputStream.
	 * (Commons-Launcher)
	 * 
	 */
	private static class StreamConnector extends Thread {
		private InputStream is = null;
		private OutputStream os = null;

		public StreamConnector(InputStream is, OutputStream os) {

			this.is = is;
			this.os = os;

		}

		public void run() {

			// If the InputStream is null, don't do anything
			if (is == null)
				return;

			// Connect the streams until the InputStream is unreadable
			try {
				int bytesRead = 0;
				byte[] buf = new byte[4096];
				while ((bytesRead = is.read(buf)) != -1) {
					if (os != null && bytesRead > 0) {
						os.write(buf, 0, bytesRead);
						os.flush();
					}
//					yield();
				}
			} catch (IOException e) {
			}

		}
	}
}
