
/*
 *  Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vision.cloud.jpa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * http://tomee.apache.org/jpa-concepts.html 很好的文章，讲了JTA和non-JTA的区别，所以Paas原来的例子是用错了JPA的
 *  * 
 */
public class VisitorServlet extends HttpServlet {

    private static String OP = "operation";


    private static String fixedPart = 
       "<html>\n" + 
       "<head>\n" + 
       "<title>Visitor</title>\n" + 
       "<link rel='stylesheet' type='text/css' href='visitors.css'>\n" + 
       "</head>\n" + 
       "<body>\n" + 
       "<div id=\"content\">\n" + 
       "<h1 id=\"welcome\">Welcome to Plymouth Aquarium</h1>\n" + 
       "<p>Please sign our visitor log</p>\n" + 
       "<div id=\"manage\">\n" + 
       "\n" + 
       "<form id=\"add\" method=\"post\">\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"create_name\">Name: </label><input type=\"text\" name=\"create_name\" value=\"\">\n" + 
       "</div>\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"create_comment\">Comment: </label><input type=\"text\" name=\"create_comment\" value=\"\">\n" + 
       "</div>\n" + 
       "<input type=\"hidden\" name=\"operation\" value=\"create\">\n" + 
       "<input type=\"submit\" value=\"Add\">\n" + 
       "</form>\n" + 
       "\n" + 
       "<form id=\"update\" method=\"post\">\n" + 
       "<p>Update Visitor:</p>\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"id_to_update\">Id: </label><input type=\"text\" name=\"id_to_update\" value=\"\">\n" + 
       "</div>\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"new_name\">Name: </label><input type=\"text\" name=\"new_name\" value=\"\">\n" + 
       "</div>\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"new_comment\">Comment: </label><input type=\"text\" name=\"new_comment\" value=\"\">\n" + 
       "</div>\n" + 
       "<input type=\"hidden\" name=\"operation\" value=\"update\">\n" + 
       "<input type=\"submit\" value=\"Update\">\n" + 
       "</form>\n" + 
       "\n" + 
       "<form id=\"delete\" method=\"post\">\n" + 
       "<p>Delete Visitor:</p>\n" + 
       "<div class=\"ctrlrow\">\n" + 
       "<label for=\"id_to_delete\">Id: </label><input type=\"text\" name=\"id_to_delete\" value=\"\">\n" + 
       "</div>\n" + 
       "<input type=\"hidden\" name=\"operation\" value=\"delete\">\n" + 
       "<input type=\"submit\" value=\"Delete\">\n" + 
       "</form>\n" + 
       "\n" + 
       "</div>\n";


       private static String endPart = 
       "</div>\n" + 
       "</body>\n" + 
       "</html>";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            out.println(fixedPart);
            try {

                String op = request.getParameter( OP );
                if ( op == null || op.length() == 0 || op.equals ("read")) {
                    ;
                } else if ( op.equals( "create")) {
                    createVisitor(request.getParameter("create_name"), 
                                  request.getParameter("create_comment"),
                                  null);
                } else if ( op.equals( "update")) {
                    updateVisitor(request.getParameter("id_to_update"), 
                                  request.getParameter("new_name"), 
                                  request.getParameter("new_comment"), 
                                  null);
                } else if ( op.equals( "delete")) {
                    deleteVisitor(request.getParameter("id_to_delete"), null);
                }
                readData(out);
                
            } catch (Throwable t) {
                out.println("Exception: " + t.getLocalizedMessage());
                t.printStackTrace(out);
            } finally {
                out.println(endPart);
            }

        } finally {
            out.close();
        }
    }


    @PersistenceContext(unitName="JPAServletPU")
    EntityManager em ;

    public int createVisitor(String name, String comment, PrintWriter out) {
        UserTransaction xact = null;
        try {
            xact = (UserTransaction)(new InitialContext().lookup("javax.transaction.UserTransaction"));
            xact.begin();
            
            em.persist(new Visitor(name, comment, new Date()));

            status("added visitor \"" + name + "\"", out);

            //em.getTransaction().commit();
            xact.commit();

            status("Transaction committed successfully.", out);
        } catch (Throwable t) {
            status("Transaction failed: " + t.getLocalizedMessage(), out);

            if ( xact != null ) {
                try {
                    xact.rollback();
                } catch (SystemException e) {
                } catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            throw new RuntimeException(t);
        }
        return 1;
    }

    void updateVisitor(String id, String newName, String newComment, PrintWriter out) {
        status("Starting update of Visitor with id = " + id + " to " + newName, out);

        UserTransaction xact = null;
        try {
            xact = (UserTransaction)(new InitialContext().lookup("javax.transaction.UserTransaction"));
            xact.begin();
            Query q = em.createQuery ("UPDATE Visitor v set v.name = :newName, v.allComments = :newComment WHERE v.id = :id" );
            q.setParameter ("newName", newName);
            q.setParameter ("newComment", newComment);
            q.setParameter ("id", Long.valueOf(id));
            int updated = q.executeUpdate ();
            
            //em.getTransaction().commit();
            xact.commit();
            status("Updated " + updated + " Visitor with id = " + id + " to " + newName, out);

            status("Transaction committed successfully.", out);
        } catch (Throwable t) {
            status("Transaction failed: " + t.getLocalizedMessage(), out);

            if ( xact != null ) {
                try {
                    xact.rollback();
                } catch (SystemException e) {
                    // @TODO log and ignore the exception
                }
            }

            throw new RuntimeException(t);
        }
    }


    public int readData(PrintWriter out) {
        int count = 0;
        try {
            Query queryVisitors = em.createQuery(
                "SELECT OBJECT(v) FROM Visitor v"
                );
            List<Visitor> visitors = queryVisitors.getResultList();
            count = visitors.size();
            out.println("<div id=\"list\">\n"); 
            out.println("<h2>Recent Visitors</h2>\n"); 
            out.println("<table>\n"); 
            out.println("<thead>\n"); 
            out.println("<tr>\n"); 
            out.println("<th>ID</th>\n"); 
            out.println("<th>Name</th>\n"); 
            out.println("<th>Visit Date</th>\n"); 
            out.println("<th>Comment</th>\n"); 
            out.println("</tr>\n"); 
            out.println("</thead>\n"); 
            out.println("<tbody>\n");

            for ( Visitor visitor : visitors) {
                out.println("<tr>\n"); 
                out.println("<td>" + visitor.getId() + "</td>\n"); 
                out.println("<td>" + visitor.getName() + "</td>\n");
                out.println("<td>" + visitor.getDateVisited() + "</td>\n");
                out.println("<td>" + visitor.getAllComments() + "</td>\n"); 
                out.println("</tr>\n"); 
            }
            out.println("</tbody>\n"); 
            out.println("</table>\n"); 
            out.println("<p>Number of Visitors <span class=\"amount\">" + count + "</span></p>\n");
            out.println("\n");
            out.println("<form method=\"post\">\n"); 
            out.println("<div class=\"ctrlrow\">\n"); 
            out.println("<input type=\"submit\" value=\"Refresh\">\n"); 
            out.println("</div>\n");
            out.println("<input type=\"hidden\" name=\"operation\" value=\"read\">\n"); 
            out.println("</form>\n");
            out.println("\n");
            out.println("</div>\n"); 
            status("Visitors read successfully. count: " + count, null);
        } catch (Throwable t) {
            status("Visitors read failed: " + t.getLocalizedMessage(), null);
            throw new RuntimeException(t);
        }
        return count;
    }
    
    int deleteVisitor(String id, PrintWriter out) {
        status("Starting delete of Visitor with id = " + id, out);
        int deleted = 0;
        UserTransaction xact = null;
        try {
            xact = (UserTransaction)(new InitialContext().lookup("javax.transaction.UserTransaction"));
            xact.begin();
            Query q = em.createQuery ("DELETE FROM Visitor v WHERE v.id = :id");
            q.setParameter ("id", Long.valueOf(id));
            deleted = q.executeUpdate ();
            
            xact.commit();
            status("Deleted " + deleted + " Visitors with id = " + id, out);

            status("Transaction committed successfully.", out);
        } catch (Throwable t) {
            status("Transaction failed: " + t.getLocalizedMessage(), out);

            if ( xact != null ) {
                try {
                    xact.rollback();
                } catch (SystemException e) {
                    // @TODO log and ignore the exception
                }
            }

            throw new RuntimeException(t);
        } 
        
        return deleted;
    }



    void status(String msg, PrintWriter out) {
        log(msg);
        
        if ( out != null ) {
            out.println("<h4>" + msg + "</h4>");
        }

    }



    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
