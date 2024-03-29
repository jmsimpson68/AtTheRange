package org.atr.services;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.atr.utils.RSODataUtils;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/api/OnDutyList")
public class OnDutyList {

	@GET()
	@Produces("application/json")
	public String getDutyList() throws UnknownHostException {
		return getRSOOnDutyList();
	}
	
	
	@GET()
	@Path("/onDuty")
	@Produces("application/json")
	public String getRSOOnDutyList() throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		Date now = new Date();
		return utils.getRSOOnDutyList(now);
		
	}
	
	@GET()
	@Path("/unvettedRSOList")
	@Produces("application/json")
	public Response getUnvettedRSOList() throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		String json = utils.getUnvettedRSOList();
		return Response.status(Response.Status.OK).entity(json).build();
		
	}
	
	@GET()
	@Path("/vettedRSOList")
	@Produces("application/json")
	public Response getVettedRSOList() throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		String json = utils.getVettedRSOList();
		return Response.status(Response.Status.OK).entity(json).build();
		
	}
		
	@GET()
	@Path("/schedule")
	@Produces("application/json")
	public String getRSOScheduleList() throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		Date now = new Date();
		return utils.getRSOScheduleList(now);
		
	}
	
	@POST()
	@Path("/vetRSO/{rsoId}")
	@Consumes("application/x-www-form-urlencoded")
	public Response postVetRSO(@PathParam("rsoId") String rsoId) throws UnknownHostException, ParseException {
		RSODataUtils utils = new RSODataUtils();
		
		utils.vetRSO(rsoId);
		
		return Response.status(Response.Status.OK).build();		
	}

	@POST()
	@Path("/unvetRSO/{rsoId}")
	@Consumes("application/x-www-form-urlencoded")
	public Response postUnvetRSO(@PathParam("rsoId") String rsoId) throws UnknownHostException, ParseException {
		RSODataUtils utils = new RSODataUtils();
		
		utils.unvetRSO(rsoId);
		
		return Response.status(Response.Status.OK).build();		
	}
	
	@POST()
	@Path("/schedule/{username}")
	@Consumes("application/x-www-form-urlencoded")
	public Response postRSOSchedule(@PathParam("username") String username, MultivaluedMap<String, String> form) throws UnknownHostException, ParseException {
		RSODataUtils utils = new RSODataUtils();
		
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		String scheduleDate = form.getFirst("scheduleDate");
		
		Date date = formatter.parse(scheduleDate);
		
		int duration = Integer.parseInt(form.getFirst("hours"));
		String scheduleType = form.getFirst("scheduleType");
		
		utils.scheduleRSO(username, date, duration, scheduleType);
		
		
		return Response.status(Response.Status.OK).build();
	}
	
	@POST()
	@Path("/unschedule/{scheduleId}")
	@Consumes("application/x-www-form-urlencoded")
	public Response postRSOUnchedule(@PathParam("scheduleId") String scheduleId) throws UnknownHostException, ParseException {
		RSODataUtils utils = new RSODataUtils();
		
				
		utils.unscheduleRSO(scheduleId);
		
		
		return Response.status(Response.Status.OK).build();
	}
	
	

	
	
	@POST()
	@Path("/onDuty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
	public String postRSOOnDuty(String jsonString) throws UnknownHostException {
		RSODataUtils utils = new RSODataUtils();
		
		DBObject dbObj = (DBObject) JSON.parse(jsonString);
		
		return utils.addNewRSOOnDuty(dbObj);
	}
	
	
	@POST()
	@Path("/goOnDuty/{username}")
	@Consumes("application/x-www-form-urlencoded")
	public Response goOnDutyList( @PathParam("username") String username, MultivaluedMap<String, String> form ) throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		
		int duration = Integer.parseInt(form.getFirst("duration"));
		
		DBObject rso = utils.getRSO(username);
		
		Calendar onDutyTime = Calendar.getInstance();
		Calendar offDutyTime = Calendar.getInstance();
		offDutyTime.add(Calendar.HOUR, duration);
		
		rso.put("onDutyAt", onDutyTime.getTime());
		rso.put("offDutyAt", offDutyTime.getTime());

		utils.rsoOnDuty(rso);
		
		return Response.status(Response.Status.OK).entity(rso.toString()).build();
		
	}
	
	@POST()
	@Path("/goOffDuty/{username}")
	@Consumes("application/x-www-form-urlencoded")
	public Response goOffDutyList( @PathParam("username") String username, MultivaluedMap<String, String> form ) throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		
		utils.rsoOffDuty(username);
		 
		return Response.status(Response.Status.OK).build();
		
	}
	
	@POST()
	@Path("/extendDuty/{username}")
	@Consumes("application/x-www-form-urlencoded")
	public Response extendTimeOnDutyList( @PathParam("username") String username, MultivaluedMap<String, String> form ) throws UnknownHostException {
		
		RSODataUtils utils = new RSODataUtils();
		
		int duration = Integer.parseInt(form.getFirst("duration"));
		
		DBObject rso = utils.getOnDutyRSO(username);
		
		Calendar offDutyTime = Calendar.getInstance();
		offDutyTime.setTime((Date) rso.get("offDutyAt"));
		offDutyTime.add(Calendar.HOUR, duration);
		
		rso.put("offDutyAt", offDutyTime.getTime());

		utils.rsoOnDuty(rso);
		
		return Response.status(Response.Status.OK).entity(rso.toString()).build();
		
	}
	
}
