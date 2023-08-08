package com.minis.batis;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.exception.mapperElementException;
import com.minis.jdbc.core.JdbcTemplate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultSqlSessionFactory implements SqlSessionFactory{

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource readDataSource;
    @Autowired
    private DataSource writeDataSource;

    String mapperLocations;

    Map<String,MapperNode> mapperNodeMap = new HashMap<>();

    public DefaultSqlSessionFactory(){}

    public void init() {
        scanLocation(this.mapperLocations); //扫描mapper文件夹
        for (Map.Entry<String, MapperNode> entry : this.mapperNodeMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Override
    public SqlSession openSession() {
        SqlSession newSqlSession = new DefaultSqlSession();
        newSqlSession.setJdbcTemplate(jdbcTemplate);
        newSqlSession.setSqlSessionFactory(this);
        // 注入读写分离数据源
        newSqlSession.setReadDataSource(this.readDataSource);
        newSqlSession.setWriteDataSource(this.writeDataSource);

        return newSqlSession;
    }


    private void scanLocation(String location) {
        String sLocationPath = this.getClass().getClassLoader().getResource("").getPath()+location;
        File dir = new File(sLocationPath);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) { //递归扫描
                scanLocation(location + "/" + file.getName());
            }else { // 处理mapper文件
                buildMapperNodes(location + "/" + file.getName());
            }
        }
    }

    private Map<String, MapperNode> buildMapperNodes(String filePath) {
        SAXReader saxReader = new SAXReader();
        URL xmlPath = this.getClass().getClassLoader().getResource(filePath);
        try {
            Document document = saxReader.read(xmlPath);
            Element rootElement = document.getRootElement();

            String namespace = rootElement.attributeValue("namespace");

            Iterator<Element> nodes = rootElement.elementIterator();
            while (nodes.hasNext()) {
                Element node = nodes.next();
                String nodeName = node.getName();
                String id = node.attributeValue("id");
                String parameterType = node.attributeValue("parameterType");
                String resultType = node.attributeValue("resultType");
                String sql = node.getText();
                String sqlType = getSqlType(nodeName);
                if ("".equals(sqlType)) {
                    throw new mapperElementException("not found this element: " + nodeName + " in "+filePath);
                }

                MapperNode selectnode = new MapperNode();
                selectnode.setNamespace(namespace);
                selectnode.setId(id);
                selectnode.setParameterType(parameterType);
                selectnode.setResultType(resultType);
                selectnode.setSql(sql);
                selectnode.setParameter("");
                selectnode.setSqlType(sqlType);

                this.mapperNodeMap.put(namespace + "." + id, selectnode);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return this.mapperNodeMap;
    }

    private String getSqlType(String nodeName) {
        if ("select".equals(nodeName)) {
            return "0";
        } else if ("update".equals(nodeName)) {
            return "1";
        } else if ("insert".equals(nodeName)) {
            return "2";
        } else if ("delete".equals(nodeName)) {
            return "3";
        }
        return "";
    }

    @Override
    public MapperNode getMapperNode(String name) {
        return this.mapperNodeMap.get(name);
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public Map<String, MapperNode> getMapperNodeMap() {
        return mapperNodeMap;
    }

    public DataSource getReadDataSource() {
        return readDataSource;
    }

    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }
}
