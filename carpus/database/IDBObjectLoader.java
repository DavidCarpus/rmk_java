package carpus.database;

import java.sql.*;
import java.util.*;

public interface IDBObjectLoader{
    public Vector load(Connection  cx, String criteria) throws Exception;
    public Vector save(Connection  cx, Vector items) throws Exception;
    public Vector remove(Connection  cx, Vector items) throws Exception;
    public String    lookup(Connection  cx, String field, String keyValue) throws Exception;
 }
