package tarea10netmaui;

import javax.swing.*;
import java.awt.*;

public class UsoCaracteristicasRedFrame extends JFrame {

    public UsoCaracteristicasRedFrame() {

        setTitle("Uso de características de red específicas de la plataforma");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        String contenido = ""
            + "Uso de características de red específicas de la plataforma\n"
            + "Completado\n"
            + "100 XP\n"
            + "5 minutos\n\n"
            + "La clase HttpClient proporciona una abstracción de la conexión a la red. Una aplicación que usa esta clase es independiente de la pila de redes de plataforma nativa. Las plantillas de .NET MAUI asignan la clase HttpClient al código que utiliza la pila de redes nativa de cada plataforma. Esto permite que una aplicación aproveche las ventajas de las características de optimización y configuración de red específicas de la plataforma. Esto es especialmente importante cuando necesita configurar una aplicación cliente para conectarse de forma segura a un servicio web de REST.\n\n"
            + "Configuración de la seguridad de transporte de aplicaciones en iOS\n"
            + "La Seguridad de transporte de aplicaciones (ATS) es una característica de iOS que requiere que todas las comunicaciones de red realizadas a través de la pila de red HTTP nativa usen TLS 1.2 o versiones posteriores. Los algoritmos de cifrado modernos no revelan información si una de las claves a largo plazo se encuentra en peligro.\n\n"
            + "Si la aplicación no cumple estas normas, se le denegará el acceso a la red. Para solucionar este problema, tiene dos opciones: puede cambiar el punto de conexión para que se adhiera a la directiva de Seguridad de transporte de aplicaciones o puede optar por no participar en la Seguridad de transporte de aplicaciones.\n\n"
            + "Para no participar en Seguridad de transporte de aplicaciones, agregue una nueva clave llamada NSAppTransportSecurity al archivo Info.plist. Encontrará el archivo Info.plist en la carpeta iOS de la carpeta Plataformas que está en el proyecto del Explorador de soluciones. Esta clave es un diccionario. Agregue otra clave denominada NSExceptionDomains a este diccionario. Esta contiene un elemento secundario para cada uno de los puntos de conexión que quiere establecer como destino. Cada punto de conexión puede tener su propia configuración y especifica qué características permite o no permite. Puede agregar esta clave mediante el editor plist genérico de Visual Studio o abriéndola como un archivo XML.\n\n"
            + "Esta es una configuración de ejemplo para un punto de conexión que se muestra como XML:\n\n"
            + "<key>NSAppTransportSecurity</key>\n"
            + "<dict>\n"
            + "   <key>NSExceptionDomains</key>\n"
            + "      <dict>\n"
            + "      <key>dotnet.microsoft.com</key>\n"
            + "      <dict>\n"
            + "        <key>NSExceptionMinimumTLSVersion</key>\n"
            + "        <string>TLSv1.0</string>\n"
            + "        <key>NSExceptionAllowsInsecureHTTPLoads</key>\n"
            + "        <true/>\n"
            + "      </dict>\n"
            + "   </dict>\n"
            + "</dict>\n\n"
            + "En este ejemplo se agrega una excepción al punto de conexión en dotnet.microsoft.com. Si está depurando un servicio de forma local en el equipo de desarrollo, puede optar por no participar en la Seguridad de transporte de aplicaciones para el tráfico local con la clave NSAllowsLocalNetworking, tal como se indica a continuación:\n\n"
            + "<key>NSAppTransportSecurity</key>\n"
            + "<dict>\n"
            + "    <key>NSAllowsLocalNetworking</key>\n"
            + "    <true/>\n"
            + "</dict>\n\n"
            + "Si no es capaz de identificar todos los puntos de conexión, deshabilite la Seguridad de transporte de aplicaciones en todos los puntos de conexión sin especificar mediante la clave NSAllowsArbitraryLoads:\n\n"
            + "<key>NSAppTransportSecurity</key>\n"
            + "<dict>\n"
            + "   <key>NSAllowsArbitraryLoads</key>\n"
            + "   <true/>\n"
            + "</dict>\n\n"
            + "Configuración de la seguridad de red de Android\n"
            + "Al igual que iOS, Android tiene un modelo de seguridad similar en torno a la comunicación de red. Este modelo se introdujo con Android 9 (nivel de API 28). El tráfico de texto no cifrado (no HTTPS) está deshabilitado de forma predeterminada cuando la aplicación tiene como destino Android 9 (nivel de API 28) o superior. Esta directiva podría afectar al ciclo de desarrollo si la aplicación necesita descargar una imagen o un archivo en un servidor que no se haya configurado para HTTPS. Además, es posible que solo esté intentando depurar la aplicación localmente y no quiera instalar certificados de desarrollo. Es posible que tenga requisitos empresariales fuertes de que todo el tráfico web de todas las versiones de Android sea siempre HTTPS. La característica Configuración de seguridad de red de Android le permite ajustar con precisión la seguridad del tráfico de red en una aplicación.\n\n"
            + "Permisos del tráfico de texto no cifrado\n"
            + "Para permitir el tráfico de texto no cifrado, cree un nuevo archivo XML en la carpeta Resources/xml, denominado network_security_config.xml (es posible que también tenga que crear la carpeta xml). La carpeta Recursos se encuentra en la carpeta de la plataforma Android del Explorador de soluciones. En este archivo, agregue un elemento network-security-config con un elemento secundario domain-config. La siguiente configuración habilita el tráfico de texto no cifrado para un dominio específico y para una dirección IP:\n\n"
            + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<network-security-config>\n"
            + "  <domain-config cleartextTrafficPermitted=\"true\">\n"
            + "    <domain includeSubdomains=\"true\">10.0.2.2</domain> <!-- Debug port -->\n"
            + "    <domain includeSubdomains=\"true\">microsoft.com</domain>\n"
            + "  </domain-config>\n"
            + "</network-security-config>\n\n"
            + "Para reforzar la seguridad de la aplicación, también puede restringir el tráfico de texto no cifrado en todas las versiones de Android, independientemente de la plataforma de destino. Para ello, establezca la propiedad cleartextTrafficPermitted del elemento domain-config en false. Esta configuración bloquea todo el tráfico que no sea HTTPS.\n\n"
            + "Para que la aplicación reconozca el archivo network_security_config.xml, configure la propiedad networkSecurityConfig del nodo application en AndroidManifest.xml que se encuentra en la carpeta Propiedades:\n\n"
            + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<manifest>\n"
            + "    <application android:networkSecurityConfig=\"@xml/network_security_config\" ...></application>\n"
            + "</manifest>\n\n"
            + "Depuración de aplicaciones de forma local\n"
            + "Un beneficio importante de la compilación de aplicaciones móviles con Visual Studio es la capacidad de ejecutar y depurar aplicaciones móviles mediante el simulador de iOS o el emulador de Android. Estas aplicaciones pueden consumir servicios web de ASP.NET Core que se ejecutan localmente y se exponen a través de HTTP.\n\n"
            + "Las aplicaciones que se ejecutan en el simulador de iOS pueden conectarse a los servicios web HTTP locales a través de la dirección IP de la máquina o a través del nombre de host de localhost. La aplicación debe anular el uso de ATS especificando un mínimo de NSAllowsLocalNetworking. Por ejemplo, dado un servicio web HTTP local que expone una operación GET mediante el identificador URI relativo /api/todoitems/, una aplicación que se ejecute en el simulador de iOS puede consumir la operación por medio del envío de una solicitud GET a http://localhost:<port>/api/todoitems/.\n\n"
            + "Las aplicaciones que se ejecutan en el emulador de Android pueden conectarse a servicios web HTTP locales a través de la dirección 10.0.2.2. Esta dirección es un alias para la interfaz de bucle invertido del host (127.0.0.1 en el equipo de desarrollo). También debe configurarse la seguridad de red para esta dirección IP específica. Por ejemplo, dado un servicio web HTTP local que expone una operación GET mediante el identificador URI relativo /api/todoitems/, una aplicación que se ejecute en el emulador de Android puede consumir la operación por medio del envío de una solicitud GET a http://10.0.2.2:/api/todoitems/.\n\n"
            + "Nota:\n"
            + "Los servicios web de ASP.NET Core que se ejecutan en modo de prueba en el host local deben tener deshabilitadas las redirecciones HTTPS; para ello, hay que comentar la instrucción app.UseHttpsRedirection(); en el archivo Startup.cs.\n\n"
            + "Detección del sistema operativo\n"
            + "Una aplicación puede determinar en qué plataforma se está ejecutando mediante la clase DeviceInfo. En el ejemplo siguiente, la aplicación establece la variable BaseAddress en un valor diferente, dependiendo de si se ejecuta en Android:\n\n"
            + "public static string BaseAddress = DeviceInfo.Platform == DevicePlatform.Android ? \"http://10.0.2.2:5000\" : \"http://localhost:5000\";\n"
            + "public static string TodoItemsUrl = $\"{BaseAddress}/api/todoitems/\";\n";

        textArea.setText(contenido);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new UsoCaracteristicasRedFrame().setVisible(true);
        });
    }
}