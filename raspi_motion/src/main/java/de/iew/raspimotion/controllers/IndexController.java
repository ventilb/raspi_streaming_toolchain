/*
 * Copyright 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.raspimotion.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Implements a simple controller to render the home web page.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 11:50
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public ModelAndView indexAction() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("pageTitle", "Raspi Motion");
        return mav;
    }
}
