using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GetTestingTypeRequest: Request
    {
      
    }

    public class GetTestingResponse: Response
    {
        public List<TestingInfor> TestingTypes;
        public List<DesignatedReason> Reasons;
        public List<TestingObject> TestingObjects;
    }

    public class TestingInfor
    {
        public int ID { get; set; }
        public string Name { get; set; }
    }

    public class DesignatedReason: TestingInfor
    {
        public List<TestingObject> Objects { get; set; }
    }

    public class TestingObject: TestingInfor
    {

    }
}