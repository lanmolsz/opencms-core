package com.opencms.file;

import java.util.*;
import javax.servlet.http.*;

import com.opencms.core.*;

/**
 * The class which extends this class gains access to the CmsRequestContext. 
 * <p>
 * In the request context are all methods bundeled, which can inform about the
 * current request, such like url or uri.
 * <p>
 * 
 * @author Andreas Schouten
 * @version $Revision: 1.10 $ $Date: 2000/02/07 10:46:45 $ 
 * 
 */
public abstract class A_CmsRequestContext
{

	/**
	 * Initializes this RequestContext.
	 * 
	 * @param req the CmsRequest.
	 * @param resp the CmsResponse.
	 * @param user The current user for this request.
	 * @param currentGroup The current group for this request.
	 * @param currentProject The current project for this request.
	 */
	abstract void init(I_CmsResourceBroker rb, I_CmsRequest req, 
					   I_CmsResponse resp, String user, String currentGroup, 
					   String currentProject) 
		throws CmsException;
	
	/**
	 * Returns the uri for this CmsObject.
	 * 
	 * @return the uri for this CmsObject.
	 */
	public abstract String getUri();
	
	/**
	 * Returns the current folder object.
	 * 
	 * @return the current folder object.
	 */
	public abstract CmsFolder currentFolder() 
		throws CmsException;

	/**
	 * Returns the current user object.
	 * 
	 * @return the current user object.
	 */
	abstract public A_CmsUser currentUser();
	

	/**
	 * Returns the current group of the current user.
	 * 
	 * @return the current group of the current user.
	 */
	abstract public A_CmsGroup currentGroup();

	/**
	 * Sets the current group of the current user.
	 * 
	 * @exception CmsException Throws CmsException if something goes wrong.
	 */
	abstract public void setCurrentGroup(String groupname) 
		throws CmsException;

	/**
	 * Determines, if the users current group is the admin-group.
	 * 
	 * @return true, if the users current group is the admin-group, 
	 * else it returns false.
	 * 
	 * @exception CmsException Throws CmsException if something goes wrong.
	 */	
	abstract public boolean isAdmin()
		throws CmsException;

	/**
	 * Determines, if the users current group is the projectleader-group.<BR>
	 * All projectleaders can create new projects, or close their own projects.
	 * 
	 * @return true, if the users current group is the projectleader-group, 
	 * else it returns false.
	 * 
	 * @exception CmsException Throws CmsException if something goes wrong.
	 */	
	abstract public  boolean isProjectManager()
		throws CmsException;

	/**
	 * Returns the current project for the user.
	 * 
	 * @return the current project for the user.
	 */
	abstract public A_CmsProject currentProject();

	/**
	 * Sets the current project for the user.
	 * 
	 * @param projectname The name of the project to be set as current.
	 * @exception CmsException Throws CmsException if something goes wrong.
	 */
	abstract public A_CmsProject setCurrentProject(String projectname)
		throws CmsException;

	/**
	 * Gets the current request, if availaible.
	 * 
	 * @return the current request, if availaible.
	 */
	public abstract I_CmsRequest getRequest();

	/**
	 * Gets the current response, if availaible.
	 * 
	 * @return the current response, if availaible.
	 */
	public abstract I_CmsResponse getResponse();
}
