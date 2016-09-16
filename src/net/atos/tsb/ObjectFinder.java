package net.atos.tsb;

import javax.persistence.*;
import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Scanner;


/**
 * Created by a451598 on 27/06/2016.
 */
public class ObjectFinder {

    Connection conn = null;
    EntityManagerFactory emf;
    EntityManager em;

    public ObjectFinder(String username, String password){

        openConnection(username, password);
    }

    public ObjectFinder(){
        openRepository();
    }

    public void close(){
        em.close();
        emf.close();
    }

    public void startHPQC(String sourcePath, String dossier){
        int procesados = 0;
        int nuevos = 0;
        int idDossier = Integer.parseInt(dossier);

        em.getTransaction().begin();

        try {
            //TruncateElements();

            ElementService service = new ElementService(em);
            //service.resetElements();

            FileInputStream in = new FileInputStream(sourcePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null){
                procesados++;
                if((procesados % 10) == 0){
                    System.out.print(String.format("Registros procesados: %d nuevos: %d\r", procesados, nuevos));
                }
                try{
                    HPQCElement el = new HPQCElement(line);
                    if(idDossier == 0 || el.dossier == idDossier) {
                        if (insertJPAElement(service, el)) nuevos++;
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                } catch (HPQCElementException e) {
                    //e.printStackTrace();
                }

            }
            System.out.println("Proceso finalizado.");

            em.getTransaction().commit();

            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } catch (IOException e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

    }

    private void TruncateElements() throws SQLException {
        String query = "TRUNCATE TABLE elements";

        Statement stmt = conn.createStatement();

        stmt.execute(query);
        stmt.close();
    }

    private void TruncateSBSistem(String subsistem) throws SQLException {
        String query = "UPDATE rochade SET estado = 'B' WHERE subsistem = ?";

        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1,subsistem);

        stmt.executeUpdate();
        stmt.close();
    }

    private void TruncateAllSBSistem() throws SQLException {
        String query = "UPDATE rochade SET estado = 'B'";

        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.executeUpdate();
        stmt.close();
    }

    private void MarkSBSistem(String subsistem) throws RecentlyUpdatedException, SQLException {
        String query = "";
        PreparedStatement stmt;

        try{
            query = "INSERT into subsistems(actualizado,codigo) VALUES(?,?)";

            stmt = conn.prepareStatement(query);

            java.util.Date fecha = Calendar.getInstance().getTime();
            stmt.setDate(1, new java.sql.Date(fecha.getTime()));
            stmt.setString(2,subsistem);

            stmt.executeUpdate();
            stmt.close();

        }catch (Exception e) {

            TestSBSistemDate(subsistem);

            query = "UPDATE subsistems SET actualizado = ? WHERE codigo = ?";

            stmt = conn.prepareStatement(query);

            java.util.Date fecha = Calendar.getInstance().getTime();
            stmt.setDate(1, new java.sql.Date(fecha.getTime()));
            stmt.setString(2, subsistem);

            stmt.executeUpdate();
            stmt.close();
        }
    }

    private void TestSBSistemDate(String subsistem) throws RecentlyUpdatedException, SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = "SELECT actualizado FROM subsistems  WHERE codigo = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1,subsistem);

        if (stmt.execute()) {
            rs = stmt.getResultSet();
            rs.first();
            Date fecha = rs.getDate(1);

            rs.close();
            stmt.close();

            Calendar current = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);

            if(cal.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)){
                throw new RecentlyUpdatedException();
            }

        }
    }

    public void start(String sourcePath, String subsistem){
        RochadeService service = new RochadeService(em);

        try {

            TruncateSBSistem(subsistem);
            try {
                MarkSBSistem(subsistem);
            } catch (RecentlyUpdatedException e) {
                Scanner reader = new Scanner(System.in);
                System.out.println("El subsistema ya ha sido actualizado hoy, desea volver a actualizar? (S/N)");

                String s = reader.next();
                while(!s.equalsIgnoreCase("S") && !s.equalsIgnoreCase("N")){
                    System.out.println("Valores válidos: (S/N)");
                    s = reader.next();
                }

                if(s.equalsIgnoreCase("N")){
                    return;
                }

            }

            String tipo = "";
            FileInputStream in = new FileInputStream(sourcePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null){

                if(line.contains("#")){
                    TSBElement el = new TSBElement(tipo,subsistem, line);
                    System.out.println(el);

                    insertRochadeElement(service, el);

                }else if(line.startsWith("--")){
                    System.out.println(line);
                }else{
                    tipo = line.trim();
                }
            }

            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void startTotal(String sourcePath){
        int procesados = 0;
        int nuevos = 0;

        RochadeService service = new RochadeService(em);


        try {

            //TruncateAllSBSistem();
            //procesados = service.resetElements();
            System.out.println(String.format("Registros actualizados: %d", procesados));

            procesados = 0;

            //em.getTransaction().begin();

            String tipo = "";
            FileInputStream in = new FileInputStream(sourcePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null){
                procesados++;
                if((procesados % 100) == 0){
                    System.out.print(String.format("Registros procesados: %d nuevos: %d\r", procesados, nuevos));
                    //em.getTransaction().commit();
                    //em.getTransaction().begin();
                }

                if(line.contains("#")){
                    TSBElement el = new TSBElement(tipo, line);

                    //em.getTransaction().begin();
                    if(insertRochadeElement(service, el)) nuevos++;
                    //em.getTransaction().commit();


                }else if(line.startsWith("--")){
                    //System.out.println(line);
                }else{
                    tipo = line.trim();
                }
            }
            if(em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }

    }

    boolean insertRochadeElement(RochadeService service, TSBElement el)  {
        boolean ret = false;

        Rochade item = service.findElementByNombre(el.nombre);

        em.getTransaction().begin();

        try {

            if (item == null) {
                Rochade nuevo = new Rochade(el);
                System.out.println(el.nombre);
                String nombre = nuevo.getNombre();
                if(nombre != null && nombre.length() > 0) {
                    service.insert(nuevo);
                    ret = true;
                }
            } else {

                Date fecha = new Date(item.getFecha_modificacion().getTime());

                if (fecha.compareTo(el.fechaModi) != 0) {
                    item.setFecha_modificacion(el.fechaModi);
                    item.setEstado('M');

                    item.setDocumento(el.documentacion ? 1 : 0);
                    item.setSubsistem(el.subsistem);
                    if(el.criticidad != null)
                        item.setCriticidad(Integer.parseInt(el.criticidad));
                    service.update(item);
                } else {
                    if(el.criticidad != null)
                        item.setCriticidad(Integer.parseInt(el.criticidad));
                    item.setEstado('I');
                    service.update(item);
                }
            }

            em.flush();
            em.clear();

            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        return ret;

        /*
        String query = "INSERT INTO rochade(nombre,documento,fecha_creacion,fecha_modificacion, tipo, subsistem, estado) VALUES(?,?,?,?,?,?,?)";

        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1,el.nombre);
        stmt.setInt(2,(el.documentacion)?1:0);
        stmt.setDate(3, new java.sql.Date(el.fechaCreacion.getTime()));
        stmt.setDate(4, new java.sql.Date(el.fechaModi.getTime()));
        stmt.setString(5,el.tipo);
        stmt.setString(6,el.subsistem);
        stmt.setString(7,"N");

        stmt.executeUpdate();
        stmt.close();
        */
    }

    void insertElement(HPQCElement el) throws SQLException {
        String query = "INSERT INTO elements(Dossier,tipo,objeto) VALUES(?,?,?)";

        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setInt(1, el.dossier);
        stmt.setString(2,el.tipo);
        stmt.setString(3,el.nombre);

        stmt.executeUpdate();
        stmt.close();
    }

    boolean insertJPAElement(ElementService service, HPQCElement el) throws SQLException {
        boolean ret = false;
        //em.getTransaction().begin();

        try {
            Element item = service.findElementByNombre(el.nombre, el.dossier);

            if (item == null) {
                item = new Element(el.nombre, el.dossier, el.tipo);
                service.insert(item);
                ret = true;
            }
            //em.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
            //em.getTransaction().rollback();

        }

        return ret;
    }

    void updateElement(TSBElement el) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = "SELECT fecha_modificacion FROM rochade  WHERE nombre = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1,el.nombre);

        if (stmt.execute()) {
            rs = stmt.getResultSet();
            rs.first();
            Date fecha = rs.getDate(1);

            if(fecha.compareTo(el.fechaModi) == 0){
                UpdateEstado(el, "I");
                return;
            }

            rs.close();
            stmt.close();
        }


        query = "UPDATE rochade SET documento = ?,fecha_creacion = ?,fecha_modificacion = ?, tipo = ?, subsistem = ?,estado = ? WHERE nombre = ?";

        stmt = conn.prepareStatement(query);

        stmt.setInt(1,(el.documentacion)?1:0);
        stmt.setDate(2, new java.sql.Date(el.fechaCreacion.getTime()));
        stmt.setDate(3, new java.sql.Date(el.fechaModi.getTime()));
        stmt.setString(4,el.tipo);
        stmt.setString(5,el.subsistem);
        stmt.setString(6, "M");
        stmt.setString(7,el.nombre);

        try{
            stmt.executeUpdate();
            stmt.close();
        }catch(SQLException e){

        }
    }

    private void UpdateEstado(TSBElement el, String estado) {
        PreparedStatement stmt = null;
        String query = "UPDATE rochade SET estado = ? WHERE nombre = ?";
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, estado);
            stmt.setString(2, el.nombre);

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start2(String path){
        File folder = new File(path);
        inspectFolder(folder.getAbsolutePath());
    }

    private void inspectFolder(String folder){
        File currentFolder = new File(folder);
        String currentDirectory = currentFolder.getAbsolutePath();
        System.out.println("Current working directory : "+currentDirectory);

        try{
            String[] files = currentFolder.list();

            if(files.length > 0){
                System.out.println(String.format("Content: %d",files.length));
                for (String fileName : files) {
                    File file = new File(fileName);
                    if(file.isDirectory()){
                        inspectFolder(file.getAbsolutePath());
                    }else {
                        System.out.println("Current file : " + file);
                    }

                }
            }
        }catch (Exception e){

        }

    }

    void openConnection(String username, String password){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/tsb?" +
                            "user="+username+"&password="+password);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void openRepository(){
        emf = Persistence.createEntityManagerFactory("JPAService");

        em = emf.createEntityManager();

        TypedQuery<Element> test = em.createQuery("FROM Element e WHERE e.Dossier = 104", Element.class);
        int i = test.getResultList().size();
        System.out.println(i);

    }

}
